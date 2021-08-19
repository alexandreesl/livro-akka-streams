package com.casadocodigo.repository

import com.casadocodigo.Boot.executionContext
import com.casadocodigo.repository.DBConnection.db
import com.casadocodigo.repository.RepositorioDePedidos.{run, tabela, tabelaClientes, tabelaProdutos, tabelaRelacionamentoProdutos}
import slick.basic.DatabasePublisher
import slick.dbio.DBIO
import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Pedido(id: Long, descricao: String, clienteId: Long) {
  def comCliente(): Future[Seq[Cliente]] = {
    DBConnection.db.run((for {
      (_, cli) <- tabela filter (_.id === id) join tabelaClientes on (_.clienteId === _.id)
    } yield cli).result)
  }

  def comProdutos(): Future[Seq[Produto]] = {
    DBConnection.db.run((for {
      (_, prod) <- (tabela filter (_.id === id) join tabelaRelacionamentoProdutos on
        (_.id === _.pedidoId)) join tabelaProdutos on (_._2.produtoId === _.id)
    } yield prod).result)
  }
}

case class PedidoProduto(pedidoId: Long, produtoId: Long, quantidade: Long)

class PedidoProdutoSchema(tag: Tag) extends Table[PedidoProduto](tag, "pedido_produto") {
  def pedidoId = column[Long]("pedido_id")

  def produtoId = column[Long]("produto_id")

  def quantidade = column[Long]("quantidade")

  def pedido = foreignKey("pedido_produto", pedidoId, tabela)(_.id)

  def produto = foreignKey("produto_pedido", produtoId, tabelaProdutos)(_.id, onDelete = ForeignKeyAction.Cascade, onUpdate = ForeignKeyAction.Cascade)

  def * = (pedidoId, produtoId, quantidade) <> (PedidoProduto.tupled, PedidoProduto.unapply)
}

class PedidoSchema(tag: Tag) extends Table[Pedido](tag, "pedido") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def descricao = column[String]("descricao")

  def clienteId = column[Long]("cliente_id")

  def cliente = foreignKey("cliente_pedido", clienteId, tabelaClientes)(_.id)

  def * = (id, descricao, clienteId) <> (Pedido.tupled, Pedido.unapply)
}

object RepositorioDePedidos extends DBConnection {
  val tabelaRelacionamentoProdutos = TableQuery[PedidoProdutoSchema]
  val tabelaProdutos = TableQuery[ProdutoSchema]
  val tabelaClientes = TableQuery[ClienteSchema]
  val tabela = TableQuery[PedidoSchema]
  db.run(DBIO.seq(
    tabela.schema.createIfNotExists,
    tabelaRelacionamentoProdutos.schema.createIfNotExists
  ))

  def criar(pedido: Pedido, produtos: List[PedidoProduto]): Future[(Pedido, Option[Int])] = {
    val commands = for {
      ped <- tabela returning tabela += pedido
      items <- tabelaRelacionamentoProdutos ++= produtos.map(prd => {
        prd.copy(pedidoId = ped.id)
      })
    } yield (ped, items)
    run(commands)
  }

  def atualizar(pedido: Pedido): Future[Int] = {
    run(tabela.filter(_.id === pedido.id).update(pedido))
  }

  def remover(pedidoId: Long): Future[Int] = {
    run(tabela.filter(_.id === pedidoId).delete)
    run(tabelaRelacionamentoProdutos.filter(_.pedidoId === pedidoId).delete)
  }

  def buscarPorId(pedidoId: Long): Future[DatabasePublisher[Pedido]] = Future {
    stream {
      tabela.filter(_.id === pedidoId).result
    }
  }
}
