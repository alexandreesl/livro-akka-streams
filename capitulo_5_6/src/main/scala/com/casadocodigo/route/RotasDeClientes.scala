package com.casadocodigo.route

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.server.Directives.{as, complete, entity, failWith, onComplete, patch, path, post, _}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Sink, Source}
import com.casadocodigo.Boot.{atorDeClientes, executionContext, scheduler, system, timeout}
import com.casadocodigo.repository.{Cliente, Endereco}
import com.casadocodigo.route.Requisicoes.RequisicaoCliente
import com.casadocodigo.route.Respostas.{RespostaBuscaClienteSucesso, RespostaSucesso}
import com.casadocodigo.service.ServicoDeClientes
import com.casadocodigo.service.ServicoDeClientes._

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.{Failure, Success}

trait RotasDeClientes extends SerializadorJSON {

  def rotasDeClientes(): Route = criarCliente() ~ atualizarCliente() ~ removerCliente() ~ buscarPorIdCliente()

  def criarCliente(): Route = post {
    path("cliente") {
      entity(as[RequisicaoCliente]) { cli =>

        val response: Future[ServicoDeClientes.RespostaCliente] = atorDeClientes.ask(ref =>
          MensagemCriarCliente(Cliente(0, cli.detalhe.nome),
            cli.enderecos.map(end =>
              Endereco(0, end.rua, end.numero, end.cidade,
                end.estado, end.cep, 0)), ref))
        onComplete(response) {
          case Success(response) => response match {
            case RespostaGerenciamentoDeCliente() => complete(RespostaSucesso(true))
            case _ => complete(BadRequest)
          }
          case Failure(e) => failWith(e)
        }
      }
    }
  }

  def atualizarCliente(): Route = patch {
    path("cliente") {
      entity(as[RequisicaoCliente]) { cli =>

        val response: Future[ServicoDeClientes.RespostaCliente] = atorDeClientes.ask(ref => MensagemAtualizarCliente(
          Cliente(cli.detalhe.id, cli.detalhe.nome),
          cli.enderecos.map(end =>
            Endereco(end.id, end.rua, end.numero, end.cidade,
              end.estado, end.cep, cli.detalhe.id)), ref))
        onComplete(response) {
          case Success(response) => response match {
            case RespostaGerenciamentoDeCliente() => complete(RespostaSucesso(true))
            case _ => complete(BadRequest)
          }
          case Failure(e) => failWith(e)
        }
      }
    }
  }

  def removerCliente(): Route = delete {
    path("cliente" / Segment) { id =>
      val response: Future[ServicoDeClientes.RespostaCliente] = atorDeClientes.ask(ref => MensagemRemoverCliente(id.toLong, ref))
      onComplete(response) {
        case Success(response) => response match {
          case RespostaGerenciamentoDeCliente() => complete(RespostaSucesso(true))
          case _ => complete(BadRequest)
        }
        case Failure(e) => failWith(e)
      }
    }
  }

  def buscarPorIdCliente(): Route = get {
    path("cliente" / "id" / Segment) { id =>
      val response: Future[ServicoDeClientes.RespostaCliente] = atorDeClientes.ask(ref => MensagemBuscarClientePorId(id.toLong, ref))
      onComplete(response) {
        case Success(response) => response match {
          case ServicoDeClientes.RespostaBuscaDeCliente(publicador) =>
            val data = Source.fromPublisher(publicador)
              .runWith(Sink.collection[Cliente, List[Cliente]])
              .map {
                listaDeClientes =>
                  RespostaBuscaClienteSucesso(listaDeClientes.head)
              }
            complete(data)
          case _ => complete(BadRequest)
        }

        case Failure(e) => failWith(e)
      }
    }
  }
}
