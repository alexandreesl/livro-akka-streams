package com.casadocodigo


import akka.actor.typed.Scheduler
import akka.actor.{ActorSystem, typed}
import akka.http.scaladsl.Http
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.casadocodigo.route.{RotasDeClientes, RotasDePedidos, RotasDeProdutos}
import com.casadocodigo.service.{ServicoDeClientes, ServicoDeEstoque, ServicoDePedidos, ServicoDeProdutos}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt
import scala.io.StdIn


object Boot extends App with RotasDeProdutos with RotasDePedidos with RotasDeClientes {

  implicit val config: Config = ConfigFactory.load(Option(
    System.getenv("ENVIRONMENT"))
    .getOrElse(Option(System.getProperty("ENVIRONMENT"))
      .getOrElse("application")))
  implicit val system: ActorSystem = akka.actor.ActorSystem("ClassicToTypedSystem")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = config.getInt("timeout").seconds
  val typedSystem: typed.ActorSystem[_] = system.toTyped
  implicit val scheduler: Scheduler = typedSystem.scheduler
  val atorDeProdutos = typedSystem.systemActorOf(ServicoDeProdutos(), "ServicoDeProdutos")
  val atorDePedidos = typedSystem.systemActorOf(ServicoDePedidos(), "ServicoDePedidos")
  val atorDeClientes = typedSystem.systemActorOf(ServicoDeClientes(), "ServicoDeClientes")
  val atorDeEstoque = typedSystem.systemActorOf(ServicoDeEstoque(), "ServicoDeEstoque")

  val route = rotasDeProdutos() ~ rotasDePedidos() ~ rotasDeClientes()

  val bindingFuture = Http().newServerAt("0.0.0.0", 8080).bind(route)

  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
