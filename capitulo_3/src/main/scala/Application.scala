import akka.actor.typed.ActorSystem
import com.casadocodigo.actors.APIActor.MensagemAPI
import com.casadocodigo.actors.{APIActor, ActorSetup}

object Application {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem(ActorSetup(), "MyActorSystem")

    val atorDB = system.systemActorOf(APIActor(), "APIActor")

    atorDB ! MensagemAPI(nome = "teste", documento = "123456")

  }

}
