package com.casadocodigo.route

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.casadocodigo.repository.Produto
import com.casadocodigo.route.Respostas.{RespostaBuscaProdutoSucesso, RespostaSucesso}
import spray.json.DefaultJsonProtocol

trait SerializadorJSON extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val respostaProdutoFormat = jsonFormat3(Produto)
  implicit val respostaSucessoFormat = jsonFormat1(RespostaSucesso)
  implicit val respostaProdutoBuscarPorIdFormat = jsonFormat1(RespostaBuscaProdutoSucesso)

}
