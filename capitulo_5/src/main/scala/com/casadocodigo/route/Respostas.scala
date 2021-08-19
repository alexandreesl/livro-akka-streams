package com.casadocodigo.route

import com.casadocodigo.repository.{Cliente, Pedido, Produto}

object Respostas {

  case class RespostaSucesso(sucesso: Boolean)

  case class RespostaBuscaProdutoSucesso(produto: List[Produto])

  case class RespostaBuscaPedidoSucesso(pedido: Pedido)

  case class RespostaBuscaClienteSucesso(cliente: Cliente)

}
