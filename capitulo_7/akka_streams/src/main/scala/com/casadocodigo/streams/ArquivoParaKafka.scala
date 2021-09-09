package com.casadocodigo.streams

import akka.NotUsed
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.alpakka.file.DirectoryChange
import akka.stream.alpakka.file.scaladsl.{Directory, DirectoryChangesSource, FileTailSource}
import akka.stream.scaladsl.{Flow, Source}
import com.casadocodigo.Boot.{config, system}
import com.casadocodigo.streams.KafkaParaBanco.Conta
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import java.io.FileNotFoundException
import java.nio.file.{FileSystems, Path}
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object ArquivoParaKafka {

  private val sistemaDeArquivos = FileSystems.getDefault
  private val diretorio = "./input_dir"
  private val mudancasNoDiretorio = DirectoryChangesSource(sistemaDeArquivos.getPath(diretorio), pollInterval = 1 second, maxBufferSize = 1000)
    .filter {
      case (_, change) =>
        change == DirectoryChange.Creation
    }
    .map {
      case (path, _) =>
        obterLeitorDeArquivo(path).merge(obterVerificadorDeArquivoDeletado(path), eagerComplete = true)
          .via(fluxoDeTransformacaoDados())
    }
  private[streams] val diretorioInicial: Source[Source[ProducerRecord[String, String], NotUsed], NotUsed] = Directory.ls(sistemaDeArquivos.getPath(diretorio))
    .map(path =>
      obterLeitorDeArquivo(path).merge(obterVerificadorDeArquivoDeletado(path), eagerComplete = true)
        .via(fluxoDeTransformacaoDados())
    )

  def fluxoDeTransformacaoDados(): Flow[String, ProducerRecord[String, String], NotUsed] = {
    Flow[String].map(value => new ProducerRecord[String, String]("contas", value))
  }


  def iniciarStreams(): Unit = {
    val kafkaProducerSettings =
      ProducerSettings.create(system, new StringSerializer(), new StringSerializer())
        .withBootstrapServers(config.getString("bootstrapServers"))

    diretorioInicial
      .runForeach(record =>
        record.runWith(Producer.plainSink(kafkaProducerSettings)
        )
      )

    mudancasNoDiretorio
      .runForeach(record =>
        record.runWith(Producer.plainSink(kafkaProducerSettings)
        )
      )
  }


  private def obterVerificadorDeArquivoDeletado(path: Path): Source[Nothing, NotUsed] = DirectoryChangesSource(path.getParent, 1 second, 8192)
    .collect {
      case (p, DirectoryChange.Deletion) if path == p =>
        throw new FileNotFoundException(path.toString)
    }
    .recoverWithRetries(1, {
      case _: FileNotFoundException => Source.empty
    })

  private def obterLeitorDeArquivo(path: Path): Source[String, NotUsed] = FileTailSource.lines(
    path = path,
    maxLineSize = 8192,
    pollingInterval = 250.millis
  )

}
