package com.casadocodigo.repository

import com.casadocodigo.repository.DBConnection.db
import slick.basic.DatabasePublisher
import slick.dbio.DBIO
import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Produto(id: Long, descricao: String, preco: Double, quantidade: Double)

class ProdutoSchema(tag: Tag) extends Table[Produto](tag, "produto") {
  def id = column[Long]("id", O.PrimaryKey)

  def descricao = column[String]("descricao")

  def preco = column[Double]("preco")

  def quantidade = column[Double]("quantidade")

  def * = (id, descricao, preco, quantidade) <> (Produto.tupled, Produto.unapply)
}

object RepositorioDeProdutos extends DBConnection {
  private val tabela = TableQuery[ProdutoSchema]
  val schema = tabela.schema
  db.run(DBIO.seq(
    schema.createIfNotExists
  ))

  def criar(produto: Produto): Future[Produto] = run {
    (tabela returning tabela) += produto
  }

  def atualizar(produto: Produto): Future[Int] = run {
    tabela.update(produto)
  }

  def remover(produtoId: Long): Future[Int] = run {
    tabela.filter(_.id === produtoId).delete
  }

  def buscarPorId(produtoId: Long): DatabasePublisher[Produto] = stream {
    tabela.filter(_.id === produtoId).result
  }

}
