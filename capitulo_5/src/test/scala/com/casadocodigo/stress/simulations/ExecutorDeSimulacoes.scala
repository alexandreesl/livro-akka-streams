package com.casadocodigo.stress.simulations

import io.gatling.core.Predef._
import scala.concurrent.duration._
import scala.language.postfixOps

class ExecutorDeSimulacoes extends Simulation with SimulacaoDeProdutos with SimulacaoDePedidos with SimulacaoDeClientes with Protocolo {

  setUp(
    cenarioProduto.inject(atOnceUsers(1)),
    cenarioCliente.inject(atOnceUsers(1)),
    cenarioCliente.inject(rampUsers(200) during (2 minutes)),
    cenarioPedido.inject(constantUsersPerSec(50) during (5 minutes))
  ).protocols(protocolo)

}
