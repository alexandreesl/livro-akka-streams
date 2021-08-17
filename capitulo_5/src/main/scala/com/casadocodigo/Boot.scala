package com.casadocodigo


import akka.actor.typed.Scheduler
import akka.actor.{ActorSystem, typed}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import com.casadocodigo.repository.{Produto, RepositorioDeProdutos}
import com.casadocodigo.service.ServicoDeProdutos
import com.casadocodigo.service.ServicoDeProdutos.MensagemCriarProduto

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration.DurationInt
import scala.io.StdIn
import scala.util.{Failure, Success}


object Boot extends App {

  implicit val system: ActorSystem = akka.actor.ActorSystem("ClassicToTypedSystem")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  val typedSystem: typed.ActorSystem[_] = system.toTyped
  implicit val timeout: Timeout = 3.seconds
  implicit val scheduler: Scheduler = typedSystem.scheduler

  val route =
    path("hello") {
      get {

        val actor = typedSystem.systemActorOf(ServicoDeProdutos(), "ServicoDeProdutos")
        val response: Future[ServicoDeProdutos.RespostaProduto] = actor.ask(ref => MensagemCriarProduto(Produto(0, "abc", 1.0), ref))
        onComplete(response) {
          case Success(response) => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, response.toString))
          case Failure(e) => failWith(e)
        }
      }
    }

  val bindingFuture = Http().newServerAt("0.0.0.0", 8080).bind(route)

  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
