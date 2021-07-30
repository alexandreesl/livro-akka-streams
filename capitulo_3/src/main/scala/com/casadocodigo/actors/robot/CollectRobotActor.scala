package com.casadocodigo.actors.robot

import akka.actor.typed.{ActorRef, Behavior, PostStop}
import akka.actor.typed.scaladsl.{Behaviors, StashBuffer}

object CollectRobotActor {

  def apply(): Behavior[ComandoDeColeta] = Behaviors.withStash(4) { buffer =>
    Behaviors.setup { contexto =>

      val refAtorDeTransmissao = contexto.spawn(TransmitRobotActor(), "TransmitRobotActor")
      val refAtorDeTransmissaoDeEmergancia = contexto.spawn(transmissaoDeEmergancia(buffer), "EmergencyTransmitActor")

      Behaviors.receive {
        (contexto, mensagem) =>
          contexto.watch(refAtorDeTransmissaoDeEmergancia)
          mensagem match {
            case Coletar(_) =>
              if (buffer.isFull) {
                Behaviors.stopped
              }
              else {
                buffer.stash(mensagem)
                Behaviors.same
              }
            case IniciarTransmissao() =>
              buffer.unstashAll(transmissor(refAtorDeTransmissao))
              Behaviors.same
          }


      }

    }
  }

  private def transmissaoDeEmergancia(stash: StashBuffer[ComandoDeColeta]): Behavior[ComandoDeColeta] = {
    Behaviors.receiveSignal[ComandoDeColeta] {
      case (contexto, _) =>
        val refAtorDeTransmissao = contexto.spawn(TransmitRobotActor(), "TransmitRobotActor")
        stash.unstashAll(transmissor(refAtorDeTransmissao))
        Behaviors.same
    }
  }

  private def transmissor(refAtorDeTransmissao: ActorRef[Transmitir]): Behavior[ComandoDeColeta] = {
    Behaviors.setup {
      _ =>
        Behaviors.receive {
          (_, mensagem) =>
            mensagem match {
              case Coletar(coleta) =>
                refAtorDeTransmissao ! Transmitir(coleta)
            }
            Behaviors.same
        }
    }
  }
}
