import java.lang.Thread.currentThread
import java.time.LocalDateTime
import java.util.concurrent.Executors
import scala.collection.mutable
import scala.concurrent.duration.MINUTES

object Application {

  def main(args: Array[String]): Unit = {

    singleThread

    multiThread

  }

  private def multiThread = {
    println(s"executando (inicio do processamento) ${LocalDateTime.now}")

    val stack = mutable.Stack[Int]()
    stack.pushAll(List.range(1, 9990000))

    val implementacao: Runnable = () => {
      if (stack.nonEmpty) stack.pop()
    }

    val servicoExecucao = Executors.newCachedThreadPool

    servicoExecucao.execute(implementacao)
    servicoExecucao.execute(implementacao)

    servicoExecucao.shutdown()

    servicoExecucao.awaitTermination(1, MINUTES)

    println(s"executando (termino do processamento) ${LocalDateTime.now}")
  }

  private def singleThread = {
    println(s"executando (inicio do processamento) ${LocalDateTime.now}")

    val stack = mutable.Stack[Int]()
    stack.pushAll(List.range(1, 9000000))

    while (stack.nonEmpty) {
      stack.pop()
    }

    println(s"executando (termino do processamento) ${LocalDateTime.now}")
  }
}
