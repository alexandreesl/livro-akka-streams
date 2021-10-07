package com.casadocodigo.route

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.server.Directives.{as, complete, entity, failWith, onComplete, patch, path, post, _}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Sink, Source}
import com.casadocodigo.Boot.{atorDeEstoque, atorDePedidos, config, executionContext, scheduler, system, timeout}
import com.casadocodigo.repository.{Pedido, PedidoProduto}
import com.casadocodigo.route.Requisicoes.RequisicaoPedido
import com.casadocodigo.route.Respostas.{RespostaBuscaPedidoSucesso, RespostaSucesso}
import com.casadocodigo.service.ServicoDeEstoque.{ConsultarEstoque, RespostaConsultaEstoque}
import com.casadocodigo.service.ServicoDePedidos
import com.casadocodigo.service.ServicoDePedidos._

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

trait RotasDePedidos extends SerializadorJSON {

  def rotasDePedidos(): Route = criarPedido() ~ atualizarPedido() ~ removerPedido() ~ buscarPorIdPedido()

  private def lift[T](futures: Seq[Future[T]]): Seq[Future[Try[T]]] =
    futures.map(_.map {
      Success(_)
    }.recover { case t => Failure(t) })

  def waitAll[T](futures: Seq[Future[T]]): Future[Seq[Try[T]]] =
    Future.sequence(lift(futures))

  def criarPedido(): Route = post {
    path("pedido") {
      entity(as[RequisicaoPedido]) { ped =>
        val hasOutOfStock = Await.result(waitAll(ped.produtos.map(item =>
          atorDeEstoque.ask(ref =>
            ConsultarEstoque(item.produtoId, ref)
          )
        )), config.getInt("timeout") seconds).map(
          msg =>
            msg.getOrElse(RespostaConsultaEstoque(0, disponivel = true))
        ).filter(msg =>
          msg match {
            case estoque: RespostaConsultaEstoque =>
              !estoque.disponivel
            case _ =>
              true
          }
        )

        if (hasOutOfStock.nonEmpty) {
          gerarRespostaDeErroDeEstoque()
        } else {
          gerarPedido(ped)
        }
      }
    }
  }

  private def gerarRespostaDeErroDeEstoque(): Route = {
    complete(BadRequest)
  }

  private def gerarPedido(ped: RequisicaoPedido): Route = {
    val response: Future[ServicoDePedidos.RespostaPedido] = atorDePedidos.ask(ref =>
      MensagemCriarPedido(Pedido(0, ped.pedido.descricao, ped.pedido.clienteId),
        ped.produtos.map({ item =>
          PedidoProduto(0, item.produtoId, item.quantidade)
        }), ref))
    onComplete(response) {
      case Success(response) => response match {
        case RespostaGerenciamentoDePedido() => complete(RespostaSucesso(true))
        case _ => complete(BadRequest)
      }
      case Failure(e) => failWith(e)
    }
  }

  def atualizarPedido(): Route = patch {
    path("pedido") {
      entity(as[Pedido]) { ped =>

        val response: Future[ServicoDePedidos.RespostaPedido] = atorDePedidos.ask(ref => MensagemAtualizarPedido(ped, ref))
        onComplete(response) {
          case Success(response) => response match {
            case RespostaGerenciamentoDePedido() => complete(RespostaSucesso(true))
            case _ => complete(BadRequest)
          }
          case Failure(e) => failWith(e)
        }
      }
    }
  }

  def removerPedido(): Route = delete {
    path("pedido" / Segment) { id =>
      val response: Future[ServicoDePedidos.RespostaPedido] = atorDePedidos.ask(ref => MensagemRemoverPedido(id.toLong, ref))
      onComplete(response) {
        case Success(response) => response match {
          case RespostaGerenciamentoDePedido() => complete(RespostaSucesso(true))
          case _ => complete(BadRequest)
        }
        case Failure(e) => failWith(e)
      }
    }
  }

  def buscarPorIdPedido(): Route = get {
    path("pedido" / "id" / Segment) { id =>
      val response: Future[ServicoDePedidos.RespostaPedido] = atorDePedidos.ask(ref => MensagemBuscarPedidoPorId(id.toLong, ref))
      onComplete(response) {
        case Success(response) => response match {
          case ServicoDePedidos.RespostaBuscaDePedido(publicador) =>
            val data = Source.fromPublisher(publicador)
              .runWith(Sink.collection[Pedido, List[Pedido]])
              .map {
                listaDePedidos =>
                  RespostaBuscaPedidoSucesso(listaDePedidos.head)
              }
            complete(data)
          case _ => complete(BadRequest)
        }

        case Failure(e) => failWith(e)
      }
    }
  }

}
