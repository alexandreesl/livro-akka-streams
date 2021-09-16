package com.casadocodigo.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object AskActor {

  trait MensagemDeRequisicao

  trait MensagemDeResposta

  case class Requisicao(mensagem: String, replyTo: ActorRef[MensagemDeResposta]) extends MensagemDeRequisicao

  case class Resposta() extends MensagemDeResposta

  def apply(): Behavior[MensagemDeRequisicao] = Behaviors.receive[MensagemDeRequisicao] {
    (contexto, mensagem) =>
      mensagem match {
        case Requisicao(mensagem, replyTo) =>
          contexto.log.info(s"processando a mensagem $mensagem!")
          replyTo ! Resposta()
          Behaviors.same
      }

  }

}
