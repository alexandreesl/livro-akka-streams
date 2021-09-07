package com.casadocodigo.streams

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.casadocodigo.Boot.{ec, system}

object PrimeiraStream {

  private val source: Source[Int, NotUsed] = Source(1 to 100)
  private[streams] val done: Source[String, NotUsed] = source
    .filter(i => i % 2 == 0)
    .map(i => f"sou o numero $i")

  def primeiraStream(): Unit = {
    done.runForeach(i => println(i))
        .onComplete(_ => println("terminando a execução!"))
  }

}
