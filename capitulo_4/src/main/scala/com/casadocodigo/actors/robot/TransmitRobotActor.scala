package com.casadocodigo.actors.robot

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object TransmitRobotActor {

  def apply(): Behavior[Transmitir] = Behaviors.receive {
    (contexto, mensagem) =>
      contexto.log.info(s"amostra $mensagem coletada transmitida com sucesso para a base de operações!")
      Behaviors.same
  }

}
