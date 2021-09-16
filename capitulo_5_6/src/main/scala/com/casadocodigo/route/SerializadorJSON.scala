package com.casadocodigo.route

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.casadocodigo.repository.{Cliente, Endereco, Pedido, PedidoProduto, Produto}
import com.casadocodigo.route.Requisicoes.{RequisicaoCliente, RequisicaoClienteDetalhe, RequisicaoClienteEndereco, RequisicaoPedido, RequisicaoPedidoDetalhe, RequisicaoPedidoItem}
import com.casadocodigo.route.Respostas.{RespostaBuscaClienteSucesso, RespostaBuscaPedidoSucesso, RespostaBuscaProdutoSucesso, RespostaSucesso}
import com.casadocodigo.service.ServicoDeEstoque.RespostaConsultaEstoque
import spray.json.DefaultJsonProtocol

trait SerializadorJSON extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val respostaProdutoFormat = jsonFormat3(Produto)
  implicit val respostaPedidoFormat = jsonFormat3(Pedido)
  implicit val respostaClienteFormat = jsonFormat2(Cliente)
  implicit val respostaEnderecoFormat = jsonFormat7(Endereco)
  implicit val respostaPedidoProdutoFormat = jsonFormat3(PedidoProduto)
  implicit val requisicaoPedidoDetalheFormat = jsonFormat2(RequisicaoPedidoDetalhe)
  implicit val requisicaoPedidoItemFormat = jsonFormat2(RequisicaoPedidoItem)
  implicit val requisicaoPedidoFormat = jsonFormat2(RequisicaoPedido)
  implicit val requisicaoClienteDetalheFormat = jsonFormat2(RequisicaoClienteDetalhe)
  implicit val requisicaoClienteEnderecoFormat = jsonFormat6(RequisicaoClienteEndereco)
  implicit val requisicaoClienteFormat = jsonFormat2(RequisicaoCliente)
  implicit val respostaSucessoFormat = jsonFormat1(RespostaSucesso)
  implicit val respostaProdutoBuscarPorIdFormat = jsonFormat1(RespostaBuscaProdutoSucesso)
  implicit val respostaPedidoBuscarPorIdFormat = jsonFormat1(RespostaBuscaPedidoSucesso)
  implicit val respostaClienteBuscarPorIdFormat = jsonFormat1(RespostaBuscaClienteSucesso)
  implicit val respostaConsultaDeEstoque = jsonFormat2(RespostaConsultaEstoque)


}
