package com.casadocodigo.streams

import akka.actor.ActorSystem
import akka.http.javadsl.Http
import org.scalamock.scalatest.MockFactory
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.scaladsl.{Keep, Sink}
import akka.stream.testkit.scaladsl.TestSource
import com.casadocodigo.streams.KafkaParaAPIs.{ChamadorHttp, fluxoDeTransformacaoDados}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.language.postfixOps

class KafkaParaAPIsSpec extends AnyFlatSpec with BeforeAndAfterAll with should.Matchers with MockFactory {

  implicit val system: ActorSystem = ActorSystem("AkkaStreams")
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  override def afterAll(): Unit = system.terminate()

  "A stream do kafka para APIs" should "ler dados do kafka para envio para as APIs" in {

    val falseador = mock[ChamadorHttp]

    (falseador.chamarHttp _).expects(_: HttpRequest).returning(Future[HttpResponse] {
      HttpResponse(status = StatusCodes.OK, entity = "{\n  \"success\": true\n}")
    }).noMoreThanTwice()

    val entrada = TestSource.probe[ConsumerRecord[String, String]]
      .via(fluxoDeTransformacaoDados(falseador))
      .toMat(Sink.ignore)(Keep.left).run()

    entrada.sendNext(new ConsumerRecord[String, String]("contas", 0,
      0, "chave", "\"Alexandre\",39,112322211"))
    entrada.sendNext(new ConsumerRecord[String, String]("contas", 0,
      0, "chave", "\"Ana Carolina\",\"18\",98274391"))
    (falseador.chamarHttp _).verify(_: HttpRequest).atLeastTwice()

  }

}
