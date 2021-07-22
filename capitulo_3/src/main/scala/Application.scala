import akka.actor.typed.ActorSystem
import com.casadocodigo.actors.{ActorSetup, DBActor}
import com.casadocodigo.actors.DBActor.MensagemBanco

object Application {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem(ActorSetup(), "MyActorSystem")

    val atorDB = system.systemActorOf(DBActor(),"DBActor")

    atorDB ! MensagemBanco(nome = "teste", documento = "123456")

  }

}
