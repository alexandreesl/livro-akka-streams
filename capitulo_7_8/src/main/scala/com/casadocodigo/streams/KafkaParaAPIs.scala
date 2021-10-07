package com.casadocodigo.streams

import akka.NotUsed
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.{ActorAttributes, FlowShape, Supervision}
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Sink, Source, Zip}
import akka.util.ByteString
import com.casadocodigo.Boot.{config, system}
import com.casadocodigo.streams.KafkaParaBanco.Conta
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import spray.json._

import java.nio.charset.StandardCharsets
import scala.language.postfixOps
import scala.util.Success

object KafkaParaAPIs extends SerializadorJSON {

  private val decider: Supervision.Decider = _ => Supervision.Restart

  private val consumerSettings =
    ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(config.getString("bootstrapServers"))
      .withGroupId("grupoAPIs")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
      .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000")

  def iniciarStreams(): Unit = {

    val grafo = Flow.fromGraph(GraphDSL.create() { implicit builder =>

      import GraphDSL.Implicits._

      val difusorMensagemKafka = builder.add(Broadcast[Conta](2))
      val juntorMensagemsAPIs = builder.add(Zip[Int, Int])

      val enviaParaFiscal = Flow[Conta].map(
        msg => {
          println(s"processando a conta ${msg.toJson.compactPrint} para o fiscal")
          (HttpRequest(method = HttpMethods.POST,
            uri = s"http://${config.getString("url")}:3000/fiscal",
            entity = HttpEntity(ContentTypes.`application/json`, msg.toJson.compactPrint)), NotUsed)
        }
      )
        .via(Http().superPool[NotUsed]())
        .map(
          resposta => {
            println(s"resposta do fiscal: $resposta")
            resposta._1.get.status.intValue()
          }
        )

      val enviaParaCredito = Flow[Conta].map(
        msg => {
          println(s"processando a conta ${msg.toJson.compactPrint} para o credito")
          (HttpRequest(method = HttpMethods.POST,
            uri = s"http://${config.getString("url")}:3000/credito",
            entity = HttpEntity(ContentTypes.`application/json`, msg.toJson.compactPrint)), NotUsed)
        }
      )
        .via(Http().superPool[NotUsed]())
        .map(
          resposta => {
            println(s"resposta do credito: $resposta")
            resposta._1.get.status.intValue()
          }
        )

      difusorMensagemKafka ~> enviaParaFiscal ~> juntorMensagemsAPIs.in0
      difusorMensagemKafka ~> enviaParaCredito ~> juntorMensagemsAPIs.in1

      FlowShape(difusorMensagemKafka.in, juntorMensagemsAPIs.out)

    })
    Consumer
      .plainSource(
        consumerSettings,
        Subscriptions.topics("contas")
      ).map(registro => {
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
      .via(grafo)
      .map(
        respostas =>
          if (respostas._1 != 200 || respostas._2 != 200) {
            println("Problema para acessar o fiscal ou o credito! Favor checar os logs.")
          } else {
            println("Conta processada com sucesso!")
          }
      )
      .withAttributes(ActorAttributes.supervisionStrategy(decider))
      .runWith(Sink.ignore)
  }

}
