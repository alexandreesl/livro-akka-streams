package com.casadocodigo


import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.alpakka.file.DirectoryChange
import akka.stream.alpakka.file.scaladsl.{Directory, DirectoryChangesSource, FileTailSource}
import akka.stream.scaladsl.Source

import java.io.FileNotFoundException
import java.nio.file.{FileSystems, Path}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.language.postfixOps


object Boot extends App {

  implicit val system: ActorSystem = ActorSystem("AkkaStreams")
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  /*implicit val personJsonWriter: JsonWriter[Map[String, ByteString]] = (input: Map[String, ByteString]) => {
    JsObject(
      input.map({
        case (k: String, v: ByteString) =>
          (k, JsString(v.utf8String))
      })
    )
  }*/

  //primeiraStream()

  val fs = FileSystems.getDefault
  private val dir = "./input_dir"
  val mudancasNoDiretorio = DirectoryChangesSource(fs.getPath(dir), pollInterval = 1 second, maxBufferSize = 1000)
  val diretorioInicial: Source[Path, NotUsed] = Directory.ls(fs.getPath(dir))
  diretorioInicial.runForeach {
    path =>
      obterVerificadorDeArquivoDeletado(path)
      obterLeitorDeArquivo(path).merge(obterVerificadorDeArquivoDeletado(path), eagerComplete = true).runForeach(linha => println(linha))
  }
  mudancasNoDiretorio.runForeach {
    case (path, change) =>
      change match {
        case DirectoryChange.Creation => obterVerificadorDeArquivoDeletado(path)
          obterLeitorDeArquivo(path).merge(obterVerificadorDeArquivoDeletado(path), eagerComplete = true).runForeach(linha => println(linha))
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

  private def primeiraStream(): Unit = {
    val source: Source[Int, NotUsed] = Source(1 to 100)
    val done: Future[Done] = source
      .filter(i => i % 2 == 0)
      .map(i => f"sou o numero $i")
      .runForeach(i => println(i))

    done.onComplete(_ => println("terminando a execução!"))
  }


}
