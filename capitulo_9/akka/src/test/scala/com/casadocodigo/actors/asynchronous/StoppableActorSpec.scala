package com.casadocodigo.actors.asynchronous

import akka.actor.testkit.typed.scaladsl.{ActorTestKit, LoggingTestKit}
import com.casadocodigo.actors.StoppableActor
import com.casadocodigo.actors.StoppableActor.{MensagemFinalizar, MensagemProcessamento}
import org.scalatest._
import org.scalatest.flatspec._
import org.scalatest.matchers._


class StoppableActorSpec extends AnyFlatSpec with BeforeAndAfterAll with should.Matchers {

  val testador = ActorTestKit()
  implicit val system = testador.system
  val ator = testador.spawn(StoppableActor(), "StoppableActor")

  override def afterAll(): Unit = testador.shutdownTestKit()

  "O ator paravel" should "processar mensagens de processamento" in {

    val dado = "Testando!"

    LoggingTestKit.info(s"processando a mensagem $dado!").expect {
      ator ! MensagemProcessamento(dado)
    }

  }

  "O ator paravel" should "processar mensagens de finalizacao" in {

    LoggingTestKit.info(s"finalizando o processamento!").expect {
      ator ! MensagemFinalizar()
    }
    val dado = "Testando!"
    LoggingTestKit.info(s"processando a mensagem $dado!").expect {
      ator ! MensagemProcessamento(dado)
    }

  }

  "O ator paravel" should "processar mensagens de finalizacao e encerrar" in {

    testador.stop(ator)
    val outroAtor = testador.spawn(StoppableActor.behavior(), "OtherStoppableActor")
    val recebedorDeadLetter = testador.createDeadLetterProbe()

    LoggingTestKit.info(s"finalizando o processamento!").expect {
      outroAtor ! MensagemFinalizar()
    }

    recebedorDeadLetter.expectTerminated(outroAtor)
  }

}
