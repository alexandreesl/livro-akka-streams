package com.casadocodigo.streams

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.casadocodigo.streams.KafkaParaBanco.Conta
import spray.json.DefaultJsonProtocol

trait SerializadorJSON extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val contaFormat = jsonFormat3(Conta)

}
