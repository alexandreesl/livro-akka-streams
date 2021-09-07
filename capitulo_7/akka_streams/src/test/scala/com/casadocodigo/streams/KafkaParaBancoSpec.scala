package com.casadocodigo.streams

import akka.actor.ActorSystem
import akka.stream.scaladsl.Keep
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import com.casadocodigo.streams.KafkaParaBanco.{Conta, fluxoDeTransformacaoDados}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.language.postfixOps

class KafkaParaBancoSpec extends AnyFlatSpec with BeforeAndAfterAll with should.Matchers {

  implicit val system: ActorSystem = ActorSystem("AkkaStreams")

  override def afterAll(): Unit = system.terminate()

  "A stream do kafka para o banco" should "ler dados do kafka para persistencia no banco" in {

    val (entrada, saida) = TestSource.probe[ConsumerRecord[String, String]]
      .via(fluxoDeTransformacaoDados())
      .toMat(TestSink[Conta]())(Keep.both).run()

    saida.request(2)
    entrada.sendNext(new ConsumerRecord[String, String]("contas", 0,
      0, "chave", "\"Alexandre\",39,112322211"))
    entrada.sendNext(new ConsumerRecord[String, String]("contas", 0,
      0, "chave", "\"Ana Carolina\",\"18\",98274391"))
    saida.expectNextUnordered(Conta("Alexandre", 39, 112322211),
      Conta("Ana Carolina", 18, 98274391))

  }

}
