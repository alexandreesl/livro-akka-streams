package com.casadocodigo.actors.synchronous

import akka.actor.testkit.typed.CapturedLogEvent
import akka.actor.testkit.typed.scaladsl.BehaviorTestKit
import com.casadocodigo.actors.CallerActor.{MensagemChamadora, MensagemSolicitarFinalizacao, MensagemSolicitarProcessamento}
import com.casadocodigo.actors.CallerActor
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.slf4j.event.Level

class CallerActorSpec extends AnyFlatSpec with should.Matchers {

  "O ator chamador" should "processar mensagens de processamento" in {

    val testador: BehaviorTestKit[MensagemChamadora] = BehaviorTestKit[MensagemChamadora](CallerActor.apply())
    val dado = "Testando!"

    testador.run(MensagemSolicitarProcessamento(dado))
    testador.logEntries() shouldBe Seq(CapturedLogEvent(Level.INFO, s"solicitando o processamento da mensagem $dado!"))

    testador.isAlive should be(true)

  }

  "O ator chamador" should "processar mensagens de finalizacao e encerrar" in {

    val testador: BehaviorTestKit[MensagemChamadora] = BehaviorTestKit[MensagemChamadora](CallerActor.apply())

    testador.run(MensagemSolicitarFinalizacao())

    testador.logEntries() shouldBe Seq(CapturedLogEvent(Level.INFO, s"solicitando a finalização do processamento!"))
    testador.isAlive should be(true)

  }

}
