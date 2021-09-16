package com.casadocodigo.route


object Requisicoes {

  case class RequisicaoPedidoDetalhe(descricao: String, clienteId: Long)
  case class RequisicaoPedidoItem(produtoId: Long, quantidade: Long)
  case class RequisicaoPedido(pedido: RequisicaoPedidoDetalhe, produtos: List[RequisicaoPedidoItem])
  case class RequisicaoClienteDetalhe(id:Long, nome: String)
  case class RequisicaoClienteEndereco(id:Long, rua: String, numero: Long, cidade: String, estado: String, cep: String)
  case class RequisicaoCliente(detalhe: RequisicaoClienteDetalhe, enderecos: List[RequisicaoClienteEndereco])

}
