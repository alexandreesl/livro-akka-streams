package com.casadocodigo.repository

import com.casadocodigo.Boot.executionContext
import com.casadocodigo.repository.DBConnection.db
import slick.basic.DatabasePublisher
import slick.dbio.DBIO
import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Produto(id: Long, descricao: String, preco: Double)

class ProdutoSchema(tag: Tag) extends Table[Produto](tag, "produto") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def descricao = column[String]("descricao")

  def preco = column[Double]("preco")

  def * = (id, descricao, preco) <> (Produto.tupled, Produto.unapply)
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
    tabela.filter(_.id === produto.id).update(produto)
  }

  def remover(produtoId: Long): Future[Int] = run {
    tabela.filter(_.id === produtoId).delete
  }

  def buscarPorId(produtoId: Long): Future[DatabasePublisher[Produto]] = Future {
    stream {
      tabela.filter(_.id === produtoId).result
    }
  }

  def buscarPorDescricao(desc: String): Future[DatabasePublisher[Produto]] = Future {
    stream {
      tabela.filter(_.descricao like s"%$desc%").result
    }
  }

}
