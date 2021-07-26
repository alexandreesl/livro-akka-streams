import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import com.casadocodigo.actors.APIActor.MensagemAPI
import com.casadocodigo.actors.{APIActor, APIClassActor, ActorSetup}

object Application {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem(ActorSetup(), "MyActorSystem")

    val atorAPI = system.systemActorOf(APIActor(), "APIActor")

    atorAPI ! MensagemAPI(nome = "teste", documento = "123456")

    val atorClassAPI = system.systemActorOf(Behaviors.setup {
      contexto: ActorContext[MensagemAPI] =>
        new APIClassActor(contexto)
    }, "APIClassActor")

    atorClassAPI ! MensagemAPI(nome = "teste 2", documento = "123456789")

  }

}
