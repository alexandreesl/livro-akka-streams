package com.casadocodigo.stress.simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

trait SimulacaoDePedidos extends UtilitarioDeNumeros {

  val cenarioPrimeiroPedido: ScenarioBuilder = scenario("CenarioDocenarioPrimeiroPedido")
    .exec(http("request_post")
      .post("/pedido").body(StringBody(
      s"""{
         |  "pedido": {
         |    "descricao": "pedido 1",
         |    "clienteId": 1
         |  },
         |  "produtos": [
         |    {
         |      "produtoId": 1,
         |      "quantidade": 3
         |    }
         |    ]
         |}""".stripMargin)).asJson)
    .pause(2 second)

  val cenarioPedido: ScenarioBuilder = scenario("CenarioDePedidos")
    .exec(http("request_post")
      .post("/pedido").body(StringBody(
      s"""{
         |  "pedido": {
         |    "descricao": "pedido 1",
         |    "clienteId": 1
         |  },
         |  "produtos": [
         |    {
         |      "produtoId": 1,
         |      "quantidade": 3
         |    }
         |    ]
         |}""".stripMargin)).asJson)

}
