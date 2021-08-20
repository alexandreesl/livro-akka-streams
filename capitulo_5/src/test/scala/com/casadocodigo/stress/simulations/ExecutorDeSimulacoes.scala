package com.casadocodigo.stress.simulations

import io.gatling.core.Predef._

class ExecutorDeSimulacoes extends Simulation with SimulacaoDeProdutos with SimulacaoDePedidos with SimulacaoDeClientes with Protocolo {

  setUp(
    cenarioProduto.inject(atOnceUsers(1)),
    cenarioCliente.inject(atOnceUsers(1)),
    cenarioPedido.inject(atOnceUsers(1))
  ).protocols(protocolo)

}
