package com.casadocodigo.route

import com.casadocodigo.repository.Produto

object Respostas {

  case class RespostaSucesso(sucesso: Boolean)

  case class RespostaBuscaProdutoSucesso(produto: List[Produto])

}
