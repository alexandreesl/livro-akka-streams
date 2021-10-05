package com.casadocodigo.actors.robot

import akka.actor.typed.{ActorRef, Behavior, PostStop}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object CollectRobotActor {

  def apply(): Behavior[ComandoDeColeta] = Behaviors.withStash(4) { buffer =>
    Behaviors.setup { contexto =>

      val refAtorDeTransmissao = contexto.spawn(TransmitRobotActor(), "TransmitRobotActor")

      Behaviors.receive[ComandoDeColeta] {
        (contexto, mensagem) =>
          mensagem match {
            case Coletar(_) =>
              contexto.scheduleOnce(2 hours, contexto.self, IniciarTransmissao)
              if (buffer.isFull) {
                contexto.log.info(s"limite de coletas atingido! Abortando operações!")
                buffer.unstashAll(transmissor(refAtorDeTransmissao))
                Behaviors.stopped
              }
              else {
                buffer.stash(mensagem)
                contexto.log.info(s"amostra $mensagem coletada com sucesso!")
                Behaviors.same
              }
            case IniciarTransmissao =>
              buffer.unstashAll(transmissor(refAtorDeTransmissao))
              Behaviors.same
          }
      }.receiveSignal {
        case (context, PostStop) =>
          context.log.info("Módulo de coletas encerrado. Favor reiniciar o módulo!")
          Behaviors.same
      }
    }
  }

  private def transmissor(refAtorDeTransmissao: ActorRef[Transmitir]): Behavior[ComandoDeColeta] = {
    Behaviors.setup {
      _ =>
        Behaviors.receive {
          (contexto, mensagem) =>
            mensagem match {
              case Coletar(coleta) =>
                refAtorDeTransmissao ! Transmitir(coleta)
              case _ =>
                contexto.log.info(s"mensagem $mensagem inválida!")
            }
            Behaviors.same
        }
    }
  }
}
