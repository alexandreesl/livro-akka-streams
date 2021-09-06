package com.casadocodigo.actors.asynchronous

import akka.actor.testkit.typed.CapturedLogEvent
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, LoggingTestKit, TestInbox}
import com.casadocodigo.actors.{AskActor, CallerActor}
import com.casadocodigo.actors.AskActor.{MensagemDeRequisicao, MensagemDeResposta, Requisicao, Resposta}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.slf4j.event.Level

class AskActorSpec extends AnyFlatSpec with BeforeAndAfterAll with should.Matchers {

  val testador = ActorTestKit()
  implicit val system = testador.system
  val ator = testador.spawn(AskActor(), "AskActor")

  override def afterAll(): Unit = testador.shutdownTestKit()

  "O ator responsivo" should "processar mensagens e nos retornar uma resposta" in {

    val dado = "Testando!"
    val recebedor = testador.createTestProbe[MensagemDeResposta]()

    LoggingTestKit.info(s"processando a mensagem $dado!").expect {
      ator ! Requisicao(dado, recebedor.ref)
    }

    recebedor.expectMessage(Resposta())
  }

}
