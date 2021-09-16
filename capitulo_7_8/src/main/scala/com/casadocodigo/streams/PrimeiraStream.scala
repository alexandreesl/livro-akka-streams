package com.casadocodigo.streams

import akka.{Done, NotUsed}
import akka.stream.scaladsl.Source
import com.casadocodigo.Boot.{ec, system}

import scala.concurrent.Future

object PrimeiraStream {

  def primeiraStream(): Unit = {
    val source: Source[Int, NotUsed] = Source(1 to 100)
    val done: Future[Done] = source
      .filter(i => i % 2 == 0)
      .map(i => f"sou o numero $i")
      .runForeach(i => println(i))

    done.onComplete(_ => println("terminando a execução!"))
  }

}
