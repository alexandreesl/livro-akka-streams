package com.casadocodigo


import com.typesafe.config.{Config, ConfigFactory}


object Boot extends App {

  implicit val config: Config = ConfigFactory.load(Option(
    System.getenv("ENVIRONMENT"))
    .getOrElse(Option(System.getProperty("ENVIRONMENT"))
      .getOrElse("application")))

}
