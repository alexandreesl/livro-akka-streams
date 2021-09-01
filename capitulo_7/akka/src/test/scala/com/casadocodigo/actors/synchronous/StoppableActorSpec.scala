package com.casadocodigo.actors.synchronous

import akka.actor.testkit.typed.scaladsl.BehaviorTestKit
import com.casadocodigo.actors.StoppableActor
import com.casadocodigo.actors.StoppableActor.{ExcecaoDeFinalizacao, Mensagem, MensagemFinalizar, MensagemProcessamento}
import org.scalatest._
import flatspec._
import matchers._


class StoppableActorSpec extends AnyFlatSpec with should.Matchers {

  "O ator paravel" should "processar mensagens de processamento" in {

    val testKit: BehaviorTestKit[Mensagem] = BehaviorTestKit[Mensagem](StoppableActor.behavior())

    testKit.run(MensagemProcessamento("Testando!"))

    testKit.isAlive should be(true)

  }

  "O ator paravel" should "processar mensagens de finalizacao e encerrar" in {

    val testKit: BehaviorTestKit[Mensagem] = BehaviorTestKit[Mensagem](StoppableActor.behavior())

    assertThrows[ExcecaoDeFinalizacao] {
      testKit.run(MensagemFinalizar())
    }

  }

}
