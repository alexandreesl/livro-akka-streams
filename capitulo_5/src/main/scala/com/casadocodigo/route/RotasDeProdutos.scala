package com.casadocodigo.route

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.server.Directives.{as, complete, entity, failWith, onComplete, patch, path, post, _}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Sink, Source}
import com.casadocodigo.Boot.{atorDeProdutos, config, scheduler, system, timeout}
import com.casadocodigo.repository.Produto
import com.casadocodigo.route.Respostas.{RespostaBuscaProdutoSucesso, RespostaSucesso}
import com.casadocodigo.service.ServicoDeProdutos
import com.casadocodigo.service.ServicoDeProdutos.{MensagemAtualizarProduto, MensagemBuscarProdutoPorDescricao, MensagemBuscarProdutoPorId, MensagemCriarProduto, MensagemRemoverProduto, RespostaGerenciamentoDeProduto}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success}

trait RotasDeProdutos extends SerializadorJSON {

  def rotasDeProdutos(): Route = criar() ~ atualizar() ~ remover() ~ buscarPorId() ~ buscarPorDescricao()

  def criar(): Route = post {
    path("produto") {
      entity(as[Produto]) { prod =>

        val response: Future[ServicoDeProdutos.RespostaProduto] = atorDeProdutos.ask(ref => MensagemCriarProduto(prod, ref))
        onComplete(response) {
          case Success(response) => response match {
            case RespostaGerenciamentoDeProduto() => complete(RespostaSucesso(true))
            case _ => complete(BadRequest)
          }
          case Failure(e) => failWith(e)
        }
      }
    }
  }

  def atualizar(): Route = patch {
    path("produto") {
      entity(as[Produto]) { prod =>

        val response: Future[ServicoDeProdutos.RespostaProduto] = atorDeProdutos.ask(ref => MensagemAtualizarProduto(prod, ref))
        onComplete(response) {
          case Success(response) => response match {
            case RespostaGerenciamentoDeProduto() => complete(RespostaSucesso(true))
            case _ => complete(BadRequest)
          }
          case Failure(e) => failWith(e)
        }
      }
    }
  }

  def remover(): Route = delete {
    path("produto" / Segment) { id =>
      val response: Future[ServicoDeProdutos.RespostaProduto] = atorDeProdutos.ask(ref => MensagemRemoverProduto(id.toLong, ref))
      onComplete(response) {
        case Success(response) => response match {
          case RespostaGerenciamentoDeProduto() => complete(RespostaSucesso(true))
          case _ => complete(BadRequest)
        }
        case Failure(e) => failWith(e)
      }
    }
  }

  def buscarPorId(): Route = get {
    path("produto" / "id" / Segment) { id =>
      val response: Future[ServicoDeProdutos.RespostaProduto] = atorDeProdutos.ask(ref => MensagemBuscarProdutoPorId(id.toLong, ref))
      onComplete(response) {
        case Success(response) => response match {
          case ServicoDeProdutos.RespostaBuscaDeProduto(publicador) =>
            complete(RespostaBuscaProdutoSucesso(Await.result(
              Source.fromPublisher(publicador)
                .runWith(Sink.collection[Produto, List[Produto]]), config.getInt("timeout") seconds)))
          case _ => complete(BadRequest)
        }

        case Failure(e) => failWith(e)
      }
    }
  }

  def buscarPorDescricao(): Route = get {
    path("produto" / "descricao" / Segment) { descricao =>
      val response: Future[ServicoDeProdutos.RespostaProduto] = atorDeProdutos.ask(ref => MensagemBuscarProdutoPorDescricao(descricao, ref))
      onComplete(response) {
        case Success(response) => response match {
          case ServicoDeProdutos.RespostaBuscaDeProduto(publicador) =>
            complete(RespostaBuscaProdutoSucesso(Await.result(
              Source.fromPublisher(publicador)
                .runWith(Sink.collection[Produto, List[Produto]]), config.getInt("timeout") seconds)))
          case _ => complete(BadRequest)
        }

        case Failure(e) => failWith(e)
      }
    }
  }

}
