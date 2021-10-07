package com.casadocodigo.streams

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.scaladsl.Keep
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import com.casadocodigo.streams.KafkaParaAPIs.fluxoDeTransformacaoDados
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.scalamock.scalatest.proxy.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.language.postfixOps
import scala.util.Try

class KafkaParaAPIsSpec extends AnyFlatSpec with BeforeAndAfterAll with should.Matchers with MockFactory {

  implicit val system: ActorSystem = ActorSystem("AkkaStreams")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val config: Config = ConfigFactory.load(Option(
    System.getenv("ENVIRONMENT"))
    .getOrElse(Option(System.getProperty("ENVIRONMENT"))
      .getOrElse("application")))

  override def afterAll(): Unit = system.terminate()

  "A stream do kafka para APIs" should "ler dados do kafka para envio para as APIs" in {

    val mock = mockFunction[HttpRequest, Future[Seq[Try[HttpResponse]]]]

    mock expects * returning Future(Seq(Try[HttpResponse] {
      HttpResponse(status = StatusCodes.OK, entity = "{\n  \"success\": true\n}")
    })) repeat 4

    val (entrada, saida) = TestSource.probe[ConsumerRecord[String, String]]
      .via(fluxoDeTransformacaoDados(mock))
      .toMat(TestSink[Unit]())(Keep.both).run()


    saida.request(2)
    entrada.sendNext(new ConsumerRecord[String, String]("contas", 0,
      0, "chave", "\"Alexandre\",39,112322211"))
    entrada.sendNext(new ConsumerRecord[String, String]("contas", 0,
      0, "chave", "\"Ana Carolina\",\"18\",98274391"))
    saida.expectNextUnordered((), ())

  }

}
