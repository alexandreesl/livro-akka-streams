package com.casadocodigo.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{Behavior, SpawnProtocol, Terminated}

object ActorSetup {

  def apply(): Behavior[SpawnProtocol.Command] = Behaviors.setup {
    contexto =>

      Behaviors.receiveSignal[SpawnProtocol.Command] {
        case (_, Terminated(_)) =>
          Behaviors.stopped
      }

      SpawnProtocol()
  }
}
