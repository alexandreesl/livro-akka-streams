package com.casadocodigo.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object DBActor {

  case class MensagemBanco(nome: String, documento: String)

  def apply(): Behavior[MensagemBanco] = Behaviors.receive {
    (contexto, mensagem) =>
      contexto.log.info(s"mensagem $mensagem recebida e persistida no banco!")
      Behaviors.same
  }

}
