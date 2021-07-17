import java.lang.Thread.currentThread
import java.time.LocalDateTime
import scala.collection.mutable

object Application {

  def main(args: Array[String]): Unit = {

    println(s"executando (inicio do processamento) ${LocalDateTime.now}")

    val stack = mutable.Stack[Int]()
    stack.pushAll(List.range(1,9000000))

    while (stack.nonEmpty) {
      stack.pop()
    }

    println(s"executando (termino do processamento) ${LocalDateTime.now}")

  }

}
