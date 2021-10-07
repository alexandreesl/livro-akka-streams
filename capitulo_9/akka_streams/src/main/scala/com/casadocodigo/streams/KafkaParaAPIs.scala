package com.casadocodigo.streams

import akka.NotUsed
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Sink, Source, Zip}
import akka.stream.{ActorAttributes, FlowShape, Supervision}
import akka.util.ByteString
import com.casadocodigo.Boot.{config, system}
import com.casadocodigo.streams.KafkaParaBanco.Conta
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import spray.json._

import java.nio.charset.StandardCharsets
import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Try

object KafkaParaAPIs extends SerializadorJSON {

  private def chamarHttp(request: HttpRequest): Future[Seq[Try[HttpResponse]]] = {
    Source.single((request, NotUsed))
      .via(Http().superPool[NotUsed]())
      .map(_._1)
      .runWith(Sink.takeLast(1))
  }


  private val decider: Supervision.Decider = _ => Supervision.Restart

  private def grafo(executorChamadaHttp: HttpRequest => Future[Seq[Try[HttpResponse]]])(implicit config: Config):
  Flow[Conta, (Int, Int), NotUsed] = Flow.fromGraph(GraphDSL.create() { implicit builder =>

    import GraphDSL.Implicits._

    val difusorMensagemKafka = builder.add(Broadcast[Conta](2))
    val juntorMensagemsAPIs = builder.add(Zip[Int, Int])

    val enviaParaFiscal = Flow[Conta].mapAsync(1)(
      msg => {
        println(s"processando a conta ${msg.toJson.compactPrint} para o fiscal")
        executorChamadaHttp(HttpRequest(method = HttpMethods.POST,
          uri = s"http://${config.getString("url")}:3000/fiscal",
          entity = HttpEntity(ContentTypes.`application/json`, msg.toJson.compactPrint)))

      }
    ).map(
      resposta => {
        println(s"resposta do fiscal: $resposta")
        resposta.head.get.status.intValue()
      }
    )

    val enviaParaCredito = Flow[Conta].mapAsync(1)(
      msg => {
        println(s"processando a conta ${msg.toJson.compactPrint} para o credito")
        executorChamadaHttp(HttpRequest(method = HttpMethods.POST,
          uri = s"http://${config.getString("url")}:3000/credito",
          entity = HttpEntity(ContentTypes.`application/json`, msg.toJson.compactPrint)))
      }
    ).map(
      resposta => {
        println(s"resposta do credito: $resposta")
        resposta.head.get.status.intValue()
      }
    )

    difusorMensagemKafka ~> enviaParaFiscal ~> juntorMensagemsAPIs.in0
    difusorMensagemKafka ~> enviaParaCredito ~> juntorMensagemsAPIs.in1

    FlowShape(difusorMensagemKafka.in, juntorMensagemsAPIs.out)

  })

  def fluxoDeTransformacaoDados(executorChamadaHttp: HttpRequest => Future[Seq[Try[HttpResponse]]] = chamarHttp)
                               (implicit config: Config): Flow[ConsumerRecord[String, String], Unit, NotUsed] = {
    Flow[ConsumerRecord[String, String]].map(registro => {
      val linhaComQuebraDelinha = registro.value() + "\n"
      ByteString(linhaComQuebraDelinha)
    }
    )
      .via(CsvParsing.lineScanner())
      .via(CsvToMap.withHeadersAsStrings(StandardCharsets.UTF_8, "nome", "idade", "documento"))
      .map(registro => Conta(registro("nome"), registro("idade").toInt, registro("documento").toLong))
      .map(conta => {
        println(s"processando a conta $conta")
        conta
      })
      .via(grafo(executorChamadaHttp))
      .map(
        respostas =>
          if (respostas._1 != 200 || respostas._2 != 200) {
            println("Problema para acessar o fiscal ou o credito! Favor checar os logs.")
          } else {
            println("Conta processada com sucesso!")
          }
      )
      .withAttributes(ActorAttributes.supervisionStrategy(decider))
  }

  def iniciarStreams(): Unit = {
    val consumerSettings =
      ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
        .withBootstrapServers(config.getString("bootstrapServers"))
        .withGroupId("grupoAPIs")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
        .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000")

    Consumer
      .plainSource(
        consumerSettings,
        Subscriptions.topics("contas")
      )
      .via(fluxoDeTransformacaoDados())
      .runWith(Sink.ignore)
  }

}
