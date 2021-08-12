package com.casadocodigo.repository

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

class RepositorioDeProdutos extends DBConnection {

  class Produto(tag: Tag) extends Table[(Long, String, Double, Double)](tag, "produto") {
    def id = column[Long]("id")

    def descricao = column[String]("descricao")

    def preco = column[Double]("preco")

    def quantidade = column[Double]("quantidade")

    def * = (id, descricao, preco, quantidade)
  }

}
