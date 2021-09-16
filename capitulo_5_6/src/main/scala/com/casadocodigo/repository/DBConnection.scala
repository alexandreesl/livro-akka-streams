package com.casadocodigo.repository

import com.typesafe.config.ConfigFactory
import slick.basic.DatabasePublisher
import slick.dbio.Effect.Read
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

trait DBConnection {
  val disableAutocommit = SimpleDBIO(_.connection.setAutoCommit(false))

  def run[T](dBIOAction: DBIOAction[T, NoStream, Nothing]): Future[T] = DBConnection.db.run(dBIOAction)

  def stream[T](dBIOAction: DBIOAction[Seq[T], Streaming[T], Read]): DatabasePublisher[T] = DBConnection.db.stream(disableAutocommit andThen dBIOAction.withStatementParameters(fetchSize = 1000))
}

object DBConnection {
  lazy val db = Database.forConfig("database.postgres", ConfigFactory.load(Option(
    System.getenv("ENVIRONMENT"))
    .getOrElse(Option(System.getProperty("ENVIRONMENT"))
      .getOrElse("application"))))
}