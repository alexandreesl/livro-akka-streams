package com.casadocodigo.streams

import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.alpakka.slick.scaladsl.Slick
import akka.util.ByteString
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import com.casadocodigo.Boot.{config, system, session}
import slick.dbio.DBIO
import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

import java.nio.charset.StandardCharsets

object KafkaParaBanco {

  case class Conta(nome: String, idade: Int, documento: Long)

  class ContaSchema(tag: Tag) extends Table[Conta](tag, "conta") {
    def nome = column[String]("nome")

    def idade = column[Int]("idade")

    def documento = column[Long]("documento")

    def * = (nome, idade, documento) <> (Conta.tupled, Conta.unapply)
  }

  private val db = session.db
  private val tabela = TableQuery[ContaSchema]
  db.run(DBIO.seq(
    tabela.schema.createIfNotExists
  ))

  private val consumerSettings =
    ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(config.getString("bootstrapServers"))
      .withGroupId("grupo")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
      .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000")

  def iniciarStreams(): Unit = {
    Consumer
      .plainSource(
        consumerSettings,
        Subscriptions.topics("contas")
      ).map(registro => {
      val linhaComQuebraDelinha = registro.value() + "\n"
      ByteString(linhaComQuebraDelinha)
    }
    )
      .via(CsvParsing.lineScanner())
      .via(CsvToMap.withHeadersAsStrings(StandardCharsets.UTF_8, "nome", "idade", "documento"))
      .map(registro => Conta(registro("nome"), registro("idade").toInt, registro("documento").toLong))
      .runWith(
        Slick.sink(conta => sqlu"INSERT INTO conta VALUES(${conta.nome}, ${conta.idade}, ${conta.documento})")
      )
  }

}
