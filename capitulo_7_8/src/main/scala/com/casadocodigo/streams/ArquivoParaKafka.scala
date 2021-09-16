package com.casadocodigo.streams

import akka.NotUsed
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.alpakka.file.DirectoryChange
import akka.stream.alpakka.file.scaladsl.{Directory, DirectoryChangesSource, FileTailSource}
import akka.stream.scaladsl.Source
import com.casadocodigo.Boot.{system, config}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import java.io.FileNotFoundException
import java.nio.file.{FileSystems, Path}
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object ArquivoParaKafka {

  private val kafkaProducerSettings =
    ProducerSettings.create(system, new StringSerializer(), new StringSerializer())
      .withBootstrapServers(config.getString("bootstrapServers"))

  private val sistemaDeArquivos = FileSystems.getDefault
  private val diretorio = "./input_dir"
  private val mudancasNoDiretorio = DirectoryChangesSource(sistemaDeArquivos.getPath(diretorio), pollInterval = 1 second, maxBufferSize = 1000)
  private val diretorioInicial: Source[Path, NotUsed] = Directory.ls(sistemaDeArquivos.getPath(diretorio))

  def iniciarStreams(): Unit = {
    diretorioInicial.runForeach {
      path =>
        obterLeitorDeArquivo(path).merge(obterVerificadorDeArquivoDeletado(path), eagerComplete = true)
          .map(value => new ProducerRecord[String, String]("contas", value))
          .runWith(Producer.plainSink(kafkaProducerSettings))
    }
    mudancasNoDiretorio.runForeach {
      case (path, change) =>
        change match {
          case DirectoryChange.Creation =>
            obterLeitorDeArquivo(path).merge(obterVerificadorDeArquivoDeletado(path), eagerComplete = true)
              .map(value => new ProducerRecord[String, String]("contas", value))
              .runWith(Producer.plainSink(kafkaProducerSettings))
        }
    }
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
