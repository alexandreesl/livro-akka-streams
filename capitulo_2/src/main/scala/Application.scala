import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable
import scala.concurrent.duration.MINUTES

object Application {

  class Recurso(val name: String) {
    def preparar(recurso: Recurso): Unit = {
      synchronized {
        println(s"sou ${name} e estou me preparando para utilizar ${recurso.name}")
        recurso.utilizando(this)
      }
    }

    def utilizando(recurso: Recurso): Unit = {
      synchronized {
        println(s"sou ${name} e estou utilizando ${recurso.name}")
      }
    }
  }

  def main(args: Array[String]): Unit = {

    singleThread

    multiThread

    sharedVar

    sharedAtomicVar

    deadlock

  }

  private def deadlock = {
    val recursoA = new Recurso("Recurso A")
    val recursoB = new Recurso("Recurso B")
    new Thread(new Runnable() {
      override def run(): Unit = {
        recursoA.preparar(recursoB)
      }
    }).start()
    new Thread(new Runnable() {
      override def run(): Unit = {
        recursoB.preparar(recursoA)
      }
    }).start()
  }

  private def sharedAtomicVar = {
    var contador: AtomicInteger = new AtomicInteger(0)

    val somador = () => {
      contador.addAndGet(1)
    }

    val redutor = () => {
      contador.addAndGet(-1)
    }

    val operador = (operacao: () => Int, identifcadorOperacao: String) => {
      for (i <- List.range(1, 120)) println(f"executando a operacao" +
        f" $identifcadorOperacao - valor do contador ${operacao()}")
    }

    val servicoExecucao = Executors.newCachedThreadPool

    servicoExecucao.execute(() => {
      operador(somador, "soma")
    })
    servicoExecucao.execute(() => {
      operador(redutor, "subtração")
    })

    servicoExecucao.shutdown()

    servicoExecucao.awaitTermination(1, MINUTES)
  }

  private def sharedVar = {
    var contador: Int = 0

    val somador = () => {
      contador += 1
      contador
    }

    val redutor = () => {
      contador -= 1
      contador
    }

    val operador = (operacao: () => Int, identifcadorOperacao: String) => {
      for (i <- List.range(1, 120)) println(f"executando a operacao" +
        f" $identifcadorOperacao - valor do contador ${operacao()}")
    }

    val servicoExecucao = Executors.newCachedThreadPool

    servicoExecucao.execute(() => {
      operador(somador, "soma")
    })
    servicoExecucao.execute(() => {
      operador(redutor, "subtração")
    })

    servicoExecucao.shutdown()

    servicoExecucao.awaitTermination(1, MINUTES)
  }

  private def multiThread = {
    println(s"executando (inicio do processamento) ${LocalDateTime.now}")

    @volatile var stack = mutable.Stack[Int]()
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
