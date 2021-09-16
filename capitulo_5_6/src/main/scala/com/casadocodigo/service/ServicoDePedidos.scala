package com.casadocodigo.service

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, SupervisorStrategy}
import akka.event.slf4j.Logger
import com.casadocodigo.Boot.executionContext
import com.casadocodigo.repository.{Pedido, PedidoProduto, RepositorioDePedidos}
import slick.basic.DatabasePublisher

import scala.language.postfixOps
import scala.util.{Failure, Success}

object ServicoDePedidos {

  val logger = Logger("ServicoDePedidos")

  trait MensagemPedido

  trait RespostaPedido

  case class MensagemCriarPedido(pedido: Pedido, produtos: List[PedidoProduto], replyTo: ActorRef[RespostaPedido]) extends MensagemPedido

  case class MensagemAtualizarPedido(pedido: Pedido, replyTo: ActorRef[RespostaPedido]) extends MensagemPedido

  case class MensagemRemoverPedido(id: Long, replyTo: ActorRef[RespostaPedido]) extends MensagemPedido

  case class MensagemBuscarPedidoPorId(id: Long, replyTo: ActorRef[RespostaPedido]) extends MensagemPedido


  case class RespostaGerenciamentoDePedido() extends RespostaPedido

  case class RespostaGerenciamentoDePedidoFalha() extends RespostaPedido

  case class RespostaBuscaDePedido(publicador: DatabasePublisher[Pedido]) extends RespostaPedido

  case class RespostaBuscaDePedidoFalha() extends RespostaPedido

  def apply(): Behavior[MensagemPedido] = Behaviors.supervise[MensagemPedido](behavior())
    .onFailure[Exception](SupervisorStrategy.restart)

  def behavior(): Behavior[MensagemPedido] = Behaviors.receive {
    (_, mensagem) =>
      mensagem match {
        case MensagemCriarPedido(pedido, produtos, replyTo) =>
          RepositorioDePedidos.criar(pedido, produtos).onComplete {
            case Success(_) => replyTo ! RespostaGerenciamentoDePedido()
            case Failure(e) => logger.error(f"erro ao tentar criar o pedido: ${e.getMessage}")
              replyTo ! RespostaGerenciamentoDePedidoFalha()
          }
          Behaviors.same
        case MensagemAtualizarPedido(pedido, replyTo) =>
          RepositorioDePedidos.atualizar(pedido).onComplete {
            case Success(_) => replyTo ! RespostaGerenciamentoDePedido()
            case Failure(e) => logger.error(f"erro ao tentar atualizar o pedido: ${e.getMessage}")
              replyTo ! RespostaGerenciamentoDePedidoFalha()
          }
          Behaviors.same
        case MensagemRemoverPedido(id, replyTo) =>
          RepositorioDePedidos.remover(id).onComplete {
            case Success(_) => replyTo ! RespostaGerenciamentoDePedido()
            case Failure(e) => logger.error(f"erro ao tentar remover o pedido: ${e.getMessage}")
              replyTo ! RespostaGerenciamentoDePedidoFalha()
          }
          Behaviors.same
        case MensagemBuscarPedidoPorId(id, replyTo) =>
          RepositorioDePedidos.buscarPorId(id).onComplete {
            case Success(response) => replyTo ! RespostaBuscaDePedido(response)
            case Failure(e) => logger.error(f"erro ao tentar buscar o pedido: ${e.getMessage}")
              replyTo ! RespostaBuscaDePedidoFalha()
          }
          Behaviors.same
      }

  }

}
