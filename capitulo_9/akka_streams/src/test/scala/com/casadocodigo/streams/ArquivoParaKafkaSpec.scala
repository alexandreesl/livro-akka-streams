package com.casadocodigo.streams

import akka.actor.ActorSystem
import akka.stream.scaladsl.Keep
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import org.apache.kafka.clients.producer.ProducerRecord
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import com.casadocodigo.streams.ArquivoParaKafka.fluxoDeTransformacaoDados

import scala.language.postfixOps

class ArquivoParaKafkaSpec extends AnyFlatSpec with BeforeAndAfterAll with should.Matchers {

  implicit val system: ActorSystem = ActorSystem("AkkaStreams")

  override def afterAll(): Unit = system.terminate()

  "A stream de arquivos para o kafka" should "enviar linhas do arquivo para persistencia no kafka" in {

    val (entrada, saida) = TestSource.probe[String]
      .via(fluxoDeTransformacaoDados())
      .toMat(TestSink[ProducerRecord[String, String]]())(Keep.both).run()
    saida.request(2)
    entrada.sendNext("\"Alexandre\",39,112322211")
    entrada.sendNext("\"Ana Carolina\",\"18\",98274391")
    saida.expectNextUnordered(new ProducerRecord("contas", "\"Alexandre\",39,112322211"),
      new ProducerRecord("contas", "\"Ana Carolina\",\"18\",98274391"))
  }

}
