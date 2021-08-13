package com.casadocodigo

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.casadocodigo.repository.{Cliente, Endereco, Produto, RepositorioDeClientes, RepositorioDeProdutos}

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn


object Boot extends App {

  implicit val system: ActorSystem = ActorSystem("system")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val route =
    path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Olá, Akka!</h1>"))
      }
    }

  val bindingFuture = Http().newServerAt("0.0.0.0", 8080).bind(route)

  RepositorioDeClientes.criar(Cliente(0, "Alexandre"),
    List(Endereco(0, "rua teste", 123, "São Paulo", "SP", "03423100", 0),
      Endereco(0, "rua teste 2", 1234, "São Paulo", "SP", "03423100", 0)))

  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
