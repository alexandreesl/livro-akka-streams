package com.casadocodigo.actors.asynchronous

import akka.actor.testkit.typed.CapturedLogEvent
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, LoggingTestKit}
import com.casadocodigo.actors.{CallerActor, StoppableActor}
import com.casadocodigo.actors.CallerActor.{MensagemChamadora, MensagemSolicitarFinalizacao, MensagemSolicitarProcessamento}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.slf4j.event.Level

class CallerActorSpec extends AnyFlatSpec with BeforeAndAfterAll with should.Matchers {

  val testador = ActorTestKit()
  implicit val system = testador.system
  val ator = testador.spawn(CallerActor(), "CallerActor")

  override def afterAll(): Unit = testador.shutdownTestKit()

  "O ator paravel" should "processar mensagens de processamento" in {

    val dado = "Testando!"

    LoggingTestKit.info(s"solicitando o processamento da mensagem $dado!").expect {
      ator ! MensagemSolicitarProcessamento(dado)
    }

  }

  "O ator paravel" should "processar mensagens de finalizacao e encerrar" in {

    LoggingTestKit.info(s"solicitando a finalização do processamento!").expect {
      ator ! MensagemSolicitarFinalizacao()
    }

  }

}
