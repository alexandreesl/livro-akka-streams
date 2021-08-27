package com.casadocodigo


import akka.actor.ActorSystem
import com.casadocodigo.streams.ArquivoParaKafka
import com.casadocodigo.streams.PrimeiraStream

import scala.concurrent.ExecutionContextExecutor
import scala.language.postfixOps


object Boot extends App {

  implicit val system: ActorSystem = ActorSystem("AkkaStreams")
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  PrimeiraStream.primeiraStream()
  ArquivoParaKafka.iniciarStreams()


}
