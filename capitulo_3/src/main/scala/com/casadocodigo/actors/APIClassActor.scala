package com.casadocodigo.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import com.casadocodigo.actors.APIActor.MensagemAPI
import com.casadocodigo.actors.DBActor.MensagemBanco

class APIClassActor(contexto: ActorContext[MensagemAPI]) extends AbstractBehavior(contexto) {

  val refAtorDB = contexto.spawn(Behaviors.setup {
    context: ActorContext[MensagemBanco] =>
      new DBClassActor(context)
  }, "DBClassActor")

  override def onMessage(msg: MensagemAPI): Behavior[MensagemAPI] = msg match {
    case MensagemAPI(nome, documento) =>
      contexto.log.info(s"mensagem ${nome} - ${documento} recebida e enviada para a API!")
      refAtorDB ! MensagemBanco(nome = nome, documento = documento)
      this
  }
}
