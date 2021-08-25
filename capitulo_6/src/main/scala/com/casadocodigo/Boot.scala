package com.casadocodigo


import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.scaladsl.Source

import scala.concurrent.{ExecutionContextExecutor, Future}


object Boot extends App {

  implicit val system: ActorSystem = ActorSystem("AkkaStreams")
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  primeiraStream

  private def primeiraStream = {
    val source: Source[Int, NotUsed] = Source(1 to 100)
    val done: Future[Done] = source.runForeach(i => println(i))

    done.onComplete(_ => println("terminando a execução!"))
  }


}
