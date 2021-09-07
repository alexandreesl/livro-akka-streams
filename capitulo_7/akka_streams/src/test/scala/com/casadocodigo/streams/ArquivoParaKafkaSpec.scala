package com.casadocodigo.streams

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorSystem
import akka.stream.scaladsl.Sink
import akka.stream.testkit.scaladsl.TestSink
import org.apache.kafka.clients.producer.ProducerRecord
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.concurrent.duration.DurationInt
import scala.concurrent.Await
import scala.language.postfixOps

class ArquivoParaKafkaSpec extends AnyFlatSpec with BeforeAndAfterAll with should.Matchers {

  private val testador = ActorTestKit()
  private implicit val system: ActorSystem[Nothing] = testador.system

  override def afterAll(): Unit = testador.shutdownTestKit()

  "A stream de arquivos para o kafka" should "enviar linhas do arquivo para persistencia no kafka" in {
    ArquivoParaKafka.diretorioInicial.map {
      registro =>
        registro.runWith(Sink.head)
    }.map(futuro => Await.result(futuro, 2 seconds))
      .runWith(TestSink[ProducerRecord[String, String]]())
      .request(1)
      .expectNext(new ProducerRecord[String, String]("contas", "\"Alexandre\",39,112322211"))

  }

}
