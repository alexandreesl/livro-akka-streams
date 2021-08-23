package com.casadocodigo.stress.simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

trait SimulacaoDeClientes extends UtilitarioDeNumeros {

  val cenarioPrimeiroCliente: ScenarioBuilder = scenario("CenarioDePrimeiroClientes")
    .exec(http("request_post")
      .post("/cliente").body(StringBody(
      s"""{
         |  "detalhe": {
         |    "id": 0,
         |    "nome": "Alexandre"
         |  },
         |  "enderecos": [
         |    {
         |      "id": 0,
         |      "rua":"rua teste 1",
         |      "numero":390,
         |      "cidade":"Sao Paulo",
         |      "estado":"SP",
         |      "cep":"12332456"
         |    },
         |    {
         |      "id": 0,
         |      "rua":"rua teste 2",
         |      "numero":158,
         |      "cidade":"Sao Paulo",
         |      "estado":"SP",
         |      "cep":"46756888"
         |    }
         |    ]
         |
         |}""".stripMargin)).asJson)

  val cenarioCliente: ScenarioBuilder = scenario("CenarioDeClientes")
    .exec(http("request_post")
      .post("/cliente").body(StringBody(
      s"""{
         |  "detalhe": {
         |    "id": 0,
         |    "nome": "Alexandre"
         |  },
         |  "enderecos": [
         |    {
         |      "id": 0,
         |      "rua":"rua teste 1",
         |      "numero":390,
         |      "cidade":"Sao Paulo",
         |      "estado":"SP",
         |      "cep":"12332456"
         |    },
         |    {
         |      "id": 0,
         |      "rua":"rua teste 2",
         |      "numero":158,
         |      "cidade":"Sao Paulo",
         |      "estado":"SP",
         |      "cep":"46756888"
         |    }
         |    ]
         |
         |}""".stripMargin)).asJson)
    .exec(http("request_patch")
      .patch("/cliente").body(StringBody(
      s"""{
         |  "detalhe": {
         |    "id": 1,
         |    "nome": "Alexandre"
         |  },
         |  "enderecos": [
         |    {
         |      "id": 2,
         |      "rua":"rua teste 123",
         |      "numero":158,
         |      "cidade":"Sao Paulo",
         |      "estado":"SP",
         |      "cep":"46756888"
         |    }
         |    ]
         |
         |}""".stripMargin)).asJson)

}
