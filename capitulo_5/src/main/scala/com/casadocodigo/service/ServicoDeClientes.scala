package com.casadocodigo.service

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, SupervisorStrategy}
import akka.event.slf4j.Logger
import com.casadocodigo.Boot.executionContext
import com.casadocodigo.repository.{Cliente, Endereco, RepositorioDeClientes}
import slick.basic.DatabasePublisher

import scala.language.postfixOps
import scala.util.{Failure, Success}

object ServicoDeClientes {

  val logger = Logger("ServicoDeClientes")

  trait MensagemCliente

  trait RespostaCliente

  case class MensagemCriarCliente(cliente: Cliente, enderecos: List[Endereco], replyTo: ActorRef[RespostaCliente]) extends MensagemCliente

  case class MensagemAtualizarCliente(cliente: Cliente, enderecos: List[Endereco], replyTo: ActorRef[RespostaCliente]) extends MensagemCliente

  case class MensagemRemoverCliente(id: Long, replyTo: ActorRef[RespostaCliente]) extends MensagemCliente

  case class MensagemBuscarClientePorId(id: Long, replyTo: ActorRef[RespostaCliente]) extends MensagemCliente


  case class RespostaGerenciamentoDeCliente() extends RespostaCliente

  case class RespostaGerenciamentoDeClienteFalha() extends RespostaCliente

  case class RespostaBuscaDeCliente(publicador: DatabasePublisher[Cliente]) extends RespostaCliente

  case class RespostaBuscaDeClienteFalha() extends RespostaCliente

  def apply(): Behavior[MensagemCliente] = Behaviors.supervise[MensagemCliente](behavior())
    .onFailure[Exception](SupervisorStrategy.restart)

  def behavior(): Behavior[MensagemCliente] = Behaviors.receive {
    (_, mensagem) =>
      mensagem match {
        case MensagemCriarCliente(cliente, enderecos, replyTo) =>
          RepositorioDeClientes.criar(cliente, enderecos).onComplete {
            case Success(_) => replyTo ! RespostaGerenciamentoDeCliente()
            case Failure(e) => logger.error(f"erro ao tentar criar o cliente: ${e.getMessage}")
              replyTo ! RespostaGerenciamentoDeClienteFalha()
          }
          Behaviors.same
        case MensagemAtualizarCliente(cliente, enderecos, replyTo) =>
          RepositorioDeClientes.atualizar(cliente, enderecos).onComplete {
            case Success(_) => replyTo ! RespostaGerenciamentoDeCliente()
            case Failure(e) => logger.error(f"erro ao tentar atualizar o cliente: ${e.getMessage}")
              replyTo ! RespostaGerenciamentoDeClienteFalha()
          }
          Behaviors.same
        case MensagemRemoverCliente(id, replyTo) =>
          RepositorioDeClientes.remover(id).onComplete {
            case Success(_) => replyTo ! RespostaGerenciamentoDeCliente()
            case Failure(e) => logger.error(f"erro ao tentar remover o cliente: ${e.getMessage}")
              replyTo ! RespostaGerenciamentoDeClienteFalha()
          }
          Behaviors.same
        case MensagemBuscarClientePorId(id, replyTo) =>
          RepositorioDeClientes.buscarPorId(id).onComplete {
            case Success(response) => replyTo ! RespostaBuscaDeCliente(response)
            case Failure(e) => logger.error(f"erro ao tentar buscar o cliente: ${e.getMessage}")
              replyTo ! RespostaBuscaDeClienteFalha()
          }
          Behaviors.same
      }

  }

}
