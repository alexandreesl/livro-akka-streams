package com.casadocodigo.service

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, SupervisorStrategy}
import akka.event.slf4j.Logger
import com.casadocodigo.Boot.executionContext
import com.casadocodigo.repository.{Produto, RepositorioDeProdutos}
import slick.basic.DatabasePublisher

import scala.language.postfixOps
import scala.util.{Failure, Success}

object ServicoDeProdutos {

  val logger = Logger("ServicoDeProdutos")

  trait MensagemProduto

  trait RespostaProduto

  case class MensagemCriarProduto(produto: Produto, replyTo: ActorRef[RespostaProduto]) extends MensagemProduto

  case class MensagemAtualizarProduto(produto: Produto, replyTo: ActorRef[RespostaProduto]) extends MensagemProduto

  case class MensagemRemoverProduto(produtoId: Long, replyTo: ActorRef[RespostaProduto]) extends MensagemProduto

  case class MensagemBuscarProdutoPorId(id: Long, replyTo: ActorRef[RespostaProduto]) extends MensagemProduto

  case class MensagemBuscarProdutoPorDescricao(descricao: String, replyTo: ActorRef[RespostaProduto]) extends MensagemProduto

  case class RespostaGerenciamentoDeProduto() extends RespostaProduto

  case class RespostaGerenciamentoDeProdutoFalha() extends RespostaProduto

  case class RespostaBuscaDeProduto(publicador: DatabasePublisher[Produto]) extends RespostaProduto

  case class RespostaBuscaDeProdutoFalha() extends RespostaProduto

  def apply(): Behavior[MensagemProduto] = Behaviors.supervise[MensagemProduto](behavior())
    .onFailure[Exception](SupervisorStrategy.restart)

  def behavior(): Behavior[MensagemProduto] = Behaviors.receive {
    (_, mensagem) =>
      mensagem match {
        case MensagemCriarProduto(produto, replyTo) =>
          RepositorioDeProdutos.criar(produto).onComplete {
            case Success(_) => replyTo ! RespostaGerenciamentoDeProduto()
            case Failure(e) => logger.error(f"erro ao tentar criar o produto: ${e.getMessage}")
              replyTo ! RespostaGerenciamentoDeProdutoFalha()
          }
          Behaviors.same
        case MensagemAtualizarProduto(produto, replyTo) =>
          RepositorioDeProdutos.atualizar(produto).onComplete {
            case Success(_) => replyTo ! RespostaGerenciamentoDeProduto()
            case Failure(e) => logger.error(f"erro ao tentar atualizar o produto: ${e.getMessage}")
              replyTo ! RespostaGerenciamentoDeProdutoFalha()
          }
          Behaviors.same
        case MensagemRemoverProduto(id, replyTo) =>
          RepositorioDeProdutos.remover(id).onComplete {
            case Success(_) => replyTo ! RespostaGerenciamentoDeProduto()
            case Failure(e) => logger.error(f"erro ao tentar remover o produto: ${e.getMessage}")
              replyTo ! RespostaGerenciamentoDeProdutoFalha()
          }
          Behaviors.same
        case MensagemBuscarProdutoPorId(id, replyTo) =>
          RepositorioDeProdutos.buscarPorId(id).onComplete {
            case Success(response) => replyTo ! RespostaBuscaDeProduto(response)
            case Failure(e) => logger.error(f"erro ao tentar buscar o produto: ${e.getMessage}")
              replyTo ! RespostaBuscaDeProdutoFalha()
          }
          Behaviors.same
        case MensagemBuscarProdutoPorDescricao(descricao, replyTo) =>
          RepositorioDeProdutos.buscarPorDescricao(descricao).onComplete {
            case Success(response) => replyTo ! RespostaBuscaDeProduto(response)
            case Failure(e) => logger.error(f"erro ao tentar buscar produtos por descricao: ${e.getMessage}")
              replyTo ! RespostaBuscaDeProdutoFalha()
          }
          Behaviors.same
      }


  }

}
