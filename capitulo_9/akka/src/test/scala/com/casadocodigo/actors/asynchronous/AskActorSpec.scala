package com.casadocodigo.actors.asynchronous

import akka.actor.testkit.typed.scaladsl.{ActorTestKit, LoggingTestKit}
import com.casadocodigo.actors.AskActor
import com.casadocodigo.actors.AskActor.{MensagemDeResposta, Requisicao, Resposta}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

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
