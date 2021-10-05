package com.casadocodigo.actors.robot

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object MoveRobotActor {

  def apply(): Behavior[ComandoDeMovimento] = Behaviors.receive {
    (contexto, mensagem) =>
      mensagem match {
        case MoverParaFrente =>
          contexto.log.info(s"Robô movido para a frente com sucesso!")
          Behaviors.same
        case MoverParaTras =>
          contexto.log.info(s"Robô movido para trás com sucesso!")
          Behaviors.same
        case MoverParaEsquerda =>
          contexto.log.info(s"Robô movido para a esquerda com sucesso!")
          Behaviors.same
        case MoverParaDireita =>
          contexto.log.info(s"Robô movido para a direita com sucesso!")
          Behaviors.same


      }
  }

}
