package com.casadocodigo.actors

import akka.actor.DeadLetter
import akka.actor.typed.{Behavior, SupervisorStrategy}
import akka.actor.typed.scaladsl.Behaviors

object DeadLetterActor {

  def apply(): Behavior[DeadLetter] = Behaviors.supervise(behavior())
    .onFailure(SupervisorStrategy.restart)

  def behavior(): Behavior[DeadLetter] = Behaviors.receive {
    (contexto, mensagem) =>
      contexto.log.info(s"processando a mensagem ${mensagem.message} da DeadLetter!")
      Behaviors.same
  }
}
