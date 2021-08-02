package com.casadocodigo.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object StoppableActor {

  trait Mensagem

  case class MensagemProcessamento(dado: String) extends Mensagem

  case class MensagemFinalizar() extends Mensagem

  def apply(): Behavior[Mensagem] = Behaviors.receive {
    (contexto, mensagem) =>
      mensagem match {
        case MensagemProcessamento(dado) =>
          contexto.log.info(s"processando a mensagem $dado!")
          Behaviors.same
        case MensagemFinalizar() =>
          contexto.log.info(s"finalizando o processamento!")
          Behaviors.stopped
      }
  }
}
