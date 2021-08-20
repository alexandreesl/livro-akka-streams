package com.casadocodigo.stress.simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

trait SimulacaoDeProdutos extends UtilitarioDeNumeros {

  val cenarioProduto: ScenarioBuilder = scenario("CenarioDeProdutos")
    .exec(http("request_post")
      .post("/produto").body(StringBody(
      s"""{ "id":0,
         |  "descricao": "teste$generateLong",
         |  "preco": 1.23 }""".stripMargin)).asJson)
    .exec(http("request_patch")
      .patch("/produto").body(StringBody(
      s"""{ "id":1,
         |  "descricao": "teste$generateLong",
         |  "preco": 1.23 }""".stripMargin)).asJson)
    .exec(http("request_get")
      .get("/produto/descricao/teste"))


}
