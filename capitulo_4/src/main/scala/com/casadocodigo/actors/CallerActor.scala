package com.casadocodigo.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.casadocodigo.actors.StoppableActor.{MensagemFinalizar, MensagemProcessamento}

object CallerActor {

  trait MensagemChamadora

  case class MensagemSolicitarProcessamento(dado: String) extends MensagemChamadora

  case class MensagemSolicitarFinalizacao() extends MensagemChamadora

  def apply(): Behavior[MensagemChamadora] = Behaviors.setup { contexto =>

    val refAtorParavel = contexto.spawn(StoppableActor(), "StoppableActor")

    Behaviors.receive {
      (contexto, mensagem) =>
        mensagem match {
          case MensagemSolicitarProcessamento(dado) =>
            contexto.log.info(s"solicitando o processamento da mensagem $dado!")
            refAtorParavel ! MensagemProcessamento(dado)
            Behaviors.same
          case MensagemSolicitarFinalizacao() =>
            contexto.log.info(s"solicitando a finalização do processamento!")
            refAtorParavel ! MensagemFinalizar()
            Behaviors.same
        }
    }
  }
}
