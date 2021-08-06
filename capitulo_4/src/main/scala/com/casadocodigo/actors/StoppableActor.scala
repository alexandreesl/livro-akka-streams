package com.casadocodigo.actors

import akka.actor.typed.{Behavior, SupervisorStrategy}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object StoppableActor {

  trait Mensagem

  case class MensagemProcessamento(dado: String) extends Mensagem

  case class MensagemFinalizar() extends Mensagem

  case class ExcecaoDeFinalizacao(mensagem: String) extends RuntimeException(mensagem)

  case class ExcecaoDeProcessamento(mensagem: String) extends RuntimeException(mensagem)

  def apply(): Behavior[Mensagem] = Behaviors.supervise(Behaviors.supervise(behavior())
    .onFailure[ExcecaoDeProcessamento](SupervisorStrategy.resume))
    .onFailure[ExcecaoDeFinalizacao](SupervisorStrategy.restartWithBackoff(1 second, 1 minute, 0.5))

  def behavior(): Behavior[Mensagem] = Behaviors.receive {
    (contexto, mensagem) =>
      mensagem match {
        case MensagemProcessamento(dado) =>
          contexto.log.info(s"processando a mensagem $dado!")
          ExcecaoDeProcessamento("Falha no processamento!")
          Behaviors.same
        case MensagemFinalizar() =>
          contexto.log.info(s"finalizando o processamento!")
          throw ExcecaoDeFinalizacao("Finalizando!")
      }
  }
}
