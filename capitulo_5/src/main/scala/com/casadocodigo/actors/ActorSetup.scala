package com.casadocodigo.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{Behavior, SpawnProtocol, Terminated}
import com.casadocodigo.service.ServicoDeProdutos

object ActorSetup {

  def apply(): Behavior[SpawnProtocol.Command] = Behaviors.setup {
    contexto =>

      contexto.spawn(ServicoDeProdutos(), "ServicoDeProdutos")

      Behaviors.receiveSignal[SpawnProtocol.Command] {
        case (_, Terminated(_)) =>
          Behaviors.stopped
      }

      SpawnProtocol()
  }
}
