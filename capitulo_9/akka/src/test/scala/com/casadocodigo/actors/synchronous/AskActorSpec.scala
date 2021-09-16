package com.casadocodigo.actors.synchronous

import akka.actor.testkit.typed.CapturedLogEvent
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import com.casadocodigo.actors.AskActor.{MensagemDeRequisicao, MensagemDeResposta, Requisicao, Resposta}
import com.casadocodigo.actors.AskActor
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.slf4j.event.Level

class AskActorSpec extends AnyFlatSpec with should.Matchers {

  "O ator responsivo" should "processar mensagens e nos retornar uma resposta" in {

    val testador: BehaviorTestKit[MensagemDeRequisicao] = BehaviorTestKit[MensagemDeRequisicao](AskActor.apply())
    val dado = "Testando!"
    val recebedor = TestInbox[MensagemDeResposta]()

    testador.run(Requisicao(dado, recebedor.ref))

    testador.logEntries() shouldBe Seq(CapturedLogEvent(Level.INFO, s"processando a mensagem $dado!"))
    recebedor.expectMessage(Resposta())
    testador.isAlive should be(true)

  }

}
