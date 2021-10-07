package com.casadocodigo.service

import akka.NotUsed
import akka.actor.typed.{ActorRef, Behavior, SupervisorStrategy}
import akka.actor.typed.scaladsl.Behaviors
import akka.event.slf4j.Logger
import akka.http.scaladsl.Http
import com.casadocodigo.Boot.{config, executionContext, system}
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import com.casadocodigo.route.SerializadorJSON
import spray.json.JsonParser

import scala.util.Success

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
          Source.single((HttpRequest(uri = s"http://${config.getString("url")}:3000/produto/$produtoId"), NotUsed))
            .via(Http().superPool[NotUsed]())
            .map {
              case (Success(res), _) =>
                res.entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
                  replyTo ! JsonParser(body.utf8String).convertTo[RespostaConsultaEstoque]
                }
                res.discardEntityBytes()
              case _ => logger.error(f"erro ao chamar o servico de estoque")
                replyTo ! RespostaConsultaEstoqueFalha()
            }
            .runWith(Sink.ignore)
      }
      Behaviors.same
  }

}
