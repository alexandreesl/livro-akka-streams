package com.casadocodigo.streams

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorSystem
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should


class PrimeiraStreamSpec extends AnyFlatSpec with BeforeAndAfterAll with should.Matchers {

  private val testador = ActorTestKit()
  private implicit val system: ActorSystem[Nothing] = testador.system

  override def afterAll(): Unit = testador.shutdownTestKit()

  "A primeira stream" should "transformar uma sequencia de numeros em uma sequencia de strings" in {
    PrimeiraStream
      .done
      .runWith(TestSink[String]())
      .request(2)
      .expectNext("sou o numero 2", "sou o numero 4")
  }

}
