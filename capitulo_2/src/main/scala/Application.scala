import java.lang.Thread.{currentThread, sleep}
import java.time.LocalDateTime

object Application {

  def main(args: Array[String]): Unit = {

    val minhaThread: Runnable = () => {
      while (true) {
        println(s"running ${LocalDateTime.now} at ${currentThread().getName}")
        sleep(1000L)
      }
    }

    val thread = new Thread(minhaThread)
    thread.run()

  }

}
