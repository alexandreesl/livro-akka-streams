package com.casadocodigo

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.Source
import com.casadocodigo.repository.{Cliente, Endereco, Pedido, PedidoProduto, Produto, RepositorioDeClientes, RepositorioDePedidos, RepositorioDeProdutos}

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

  Source.fromPublisher(RepositorioDePedidos.buscarPorId(1))
    .runForeach(p => {
      println(p)
      p.comCliente().foreach(
        cli => println(cli)
      )
      p.comProdutos().foreach(
        prod => {
          println(prod)
        }
      )
    })

  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
