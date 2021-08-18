package com.casadocodigo.route

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.casadocodigo.repository.{Pedido, PedidoProduto, Produto}
import com.casadocodigo.route.Requisicoes.{RequisicaoPedido, RequisicaoPedidoDetalhe, RequisicaoPedidoItem}
import com.casadocodigo.route.Respostas.{RespostaBuscaPedidoSucesso, RespostaBuscaProdutoSucesso, RespostaSucesso}
import spray.json.DefaultJsonProtocol

trait SerializadorJSON extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val respostaProdutoFormat = jsonFormat3(Produto)
  implicit val respostaPedidoFormat = jsonFormat3(Pedido)
  implicit val respostaPedidoProdutoFormat = jsonFormat3(PedidoProduto)
  implicit val requisicaoPedidoDetalheFormat = jsonFormat2(RequisicaoPedidoDetalhe)
  implicit val requisicaoPedidoItemFormat = jsonFormat2(RequisicaoPedidoItem)
  implicit val requisicaoPedidoFormat = jsonFormat2(RequisicaoPedido)
  implicit val respostaSucessoFormat = jsonFormat1(RespostaSucesso)
  implicit val respostaProdutoBuscarPorIdFormat = jsonFormat1(RespostaBuscaProdutoSucesso)
  implicit val respostaPedidoBuscarPorIdFormat = jsonFormat1(RespostaBuscaPedidoSucesso)

}
