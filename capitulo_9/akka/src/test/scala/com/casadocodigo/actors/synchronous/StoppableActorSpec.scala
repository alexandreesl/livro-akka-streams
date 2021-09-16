package com.casadocodigo.actors.synchronous

import akka.actor.testkit.typed.CapturedLogEvent
import akka.actor.testkit.typed.scaladsl.BehaviorTestKit
import com.casadocodigo.actors.StoppableActor
import com.casadocodigo.actors.StoppableActor.{ExcecaoDeFinalizacao, Mensagem, MensagemFinalizar, MensagemProcessamento}
import org.scalatest._
import flatspec._
import matchers._
import org.slf4j.event.Level


class StoppableActorSpec extends AnyFlatSpec with should.Matchers {

  "O ator paravel" should "processar mensagens de processamento" in {

    val testador: BehaviorTestKit[Mensagem] = BehaviorTestKit[Mensagem](StoppableActor.behavior())

    val dado = "Testando!"

    testador.run(MensagemProcessamento(dado))

    testador.logEntries() shouldBe Seq(CapturedLogEvent(Level.INFO, s"processando a mensagem $dado!"))
    testador.isAlive should be(true)

  }

  "O ator paravel" should "processar mensagens de finalizacao e encerrar" in {

    val testador: BehaviorTestKit[Mensagem] = BehaviorTestKit[Mensagem](StoppableActor.behavior())

    assertThrows[ExcecaoDeFinalizacao] {
      testador.run(MensagemFinalizar())
    }

  }

}
