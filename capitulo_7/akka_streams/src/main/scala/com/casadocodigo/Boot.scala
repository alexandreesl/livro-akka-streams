package com.casadocodigo


import akka.actor.ActorSystem
import akka.stream.alpakka.slick.scaladsl.SlickSession
import com.casadocodigo.streams.{ArquivoParaKafka, KafkaParaAPIs, KafkaParaBanco, PrimeiraStream}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContextExecutor
import scala.language.postfixOps


object Boot extends App {

  implicit val config: Config = ConfigFactory.load(Option(
    System.getenv("ENVIRONMENT"))
    .getOrElse(Option(System.getProperty("ENVIRONMENT"))
      .getOrElse("application")))
  implicit val system: ActorSystem = ActorSystem("AkkaStreams")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val session: SlickSession = SlickSession.forConfig("slick-postgres")
  system.registerOnTermination(() => session.close())

  PrimeiraStream.primeiraStream()
  ArquivoParaKafka.iniciarStreams()
  KafkaParaBanco.iniciarStreams()
  KafkaParaAPIs.iniciarStreams()

}
