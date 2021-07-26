package com.casadocodigo.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext}
import com.casadocodigo.actors.DBActor.MensagemBanco

class DBClassActor(contexto: ActorContext[MensagemBanco]) extends AbstractBehavior(contexto) {
  override def onMessage(msg: MensagemBanco): Behavior[MensagemBanco] = msg match {
    case MensagemBanco(nome, documento) =>
      contexto.log.info(s"mensagem ${nome} - ${documento} recebida e persistida no banco!")
      this
  }
}
