package com.casadocodigo.route


object Requisicoes {

  case class RequisicaoPedidoDetalhe(descricao: String, clienteId: Long)
  case class RequisicaoPedidoItem(produtoId: Long, quantidade: Long)
  case class RequisicaoPedido(pedido: RequisicaoPedidoDetalhe, produtos: List[RequisicaoPedidoItem])

}
