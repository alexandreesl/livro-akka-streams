package com.casadocodigo.service

import akka.actor.typed.{ActorRef, Behavior, SupervisorStrategy}
import akka.actor.typed.scaladsl.Behaviors
import akka.event.slf4j.Logger
import akka.http.scaladsl.Http
import com.casadocodigo.Boot.{executionContext, system}
import akka.http.scaladsl.model._
import akka.util.ByteString
import com.casadocodigo.route.SerializadorJSON
import spray.json.JsonParser

import scala.concurrent.Future
import scala.util.{Failure, Success}

object ServicoDeEstoque extends SerializadorJSON {

  val logger = Logger("ServicoDeEstoque")

  trait MensagemEstoque

  case class ConsultarEstoque(produtoId: Long, replyTo: ActorRef[MensagemEstoque])

  case class RespostaConsultaEstoque(produtoId: Long, disponivel: Boolean) extends MensagemEstoque

  case class RespostaConsultaEstoqueFalha() extends MensagemEstoque

  def apply(): Behavior[ConsultarEstoque] = Behaviors.supervise[ConsultarEstoque](behavior())
    .onFailure[Exception](SupervisorStrategy.restart)

  def behavior(): Behavior[ConsultarEstoque] = Behaviors.receive {
    (_, mensagem) =>
      mensagem match {
        case ConsultarEstoque(produtoId, replyTo) =>
          val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "https://run.mocky.io/v3/c65f1dd6-eb72-4cb6-8130-0426ba4f4ad0"))
          responseFuture
            .onComplete {
              case Success(res) =>
                res.entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
                  replyTo ! JsonParser(body.utf8String).convertTo[RespostaConsultaEstoque]
                }
              case Failure(e) => logger.error(f"erro ao chamar o servico de estoque ${e.getMessage}")
                replyTo ! RespostaConsultaEstoqueFalha()
            }
      }
      Behaviors.same
  }

}
