package com.casadocodigo.repository

import com.casadocodigo.Boot.executionContext
import com.casadocodigo.repository.DBConnection.db
import com.casadocodigo.repository.RepositorioDeClientes.{tabela, tabelaFilha}
import slick.basic.DatabasePublisher
import slick.dbio.DBIO
import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Cliente(id: Long, nome: String) {
  def comEnderecos(): Future[Seq[Endereco]] = {
    DBConnection.db.run((for {
      (_, end) <- tabela filter (_.id === id) join tabelaFilha on (_.id === _.clienteId)
    } yield end).result)
  }
}

class ClienteSchema(tag: Tag) extends Table[Cliente](tag, "cliente") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def nome = column[String]("nome")

  def * = (id, nome) <> (Cliente.tupled, Cliente.unapply)
}

case class Endereco(id: Long, rua: String, numero: Long, cidade: String, estado: String, cep: String, clienteId: Long)

class EnderecoSchema(tag: Tag) extends Table[Endereco](tag, "endereco") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def rua = column[String]("rua")

  def numero = column[Long]("numero")

  def cidade = column[String]("cidade")

  def estado = column[String]("estado")

  def cep = column[String]("cep")

  def clienteId = column[Long]("cliente_id")

  def cliente = foreignKey("cliente", clienteId, tabela)(_.id, onDelete = ForeignKeyAction.Cascade)


  def * = (id, rua, numero, cidade, estado, cep, clienteId) <> (Endereco.tupled, Endereco.unapply)
}

object RepositorioDeClientes extends DBConnection {
  val tabelaFilha = TableQuery[EnderecoSchema]
  val tabela = TableQuery[ClienteSchema]
  db.run(DBIO.seq(
    tabela.schema.createIfNotExists,
    tabelaFilha.schema.createIfNotExists
  ))

  def criar(cliente: Cliente, enderecos: List[Endereco]): Future[Option[Int]] = {
    val commands = for {
      cliente <- tabela returning tabela += cliente
      endrs <- tabelaFilha ++= enderecos.map(end => {
        end.copy(clienteId = cliente.id)
      })
    } yield endrs
    run(commands)
  }

  def atualizar(cliente: Cliente, enderecos: List[Endereco]): Future[Int] = {
    enderecos.foreach(end => {
      run(tabelaFilha.update(end))
    })
    run(tabela.update(cliente))
  }

  def remover(clienteId: Long): Future[Int] = run {
    tabela.filter(_.id === clienteId).delete
  }

  def buscarPorId(clienteId: Long): DatabasePublisher[Cliente] = stream {
    tabela.filter(_.id === clienteId).result
  }

}
