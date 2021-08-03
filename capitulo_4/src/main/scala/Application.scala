import akka.actor.typed.{ActorRef, ActorSystem, Props, SpawnProtocol}
import akka.util.Timeout
import com.casadocodigo.actors.{ActorSetup, CallerActor}
import com.casadocodigo.actors.CallerActor.{MensagemChamadora, MensagemSolicitarFinalizacao, MensagemSolicitarProcessamento}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

object Application {

  def main(args: Array[String]): Unit = {

    import akka.actor.typed.scaladsl.AskPattern._
    implicit val system: ActorSystem[SpawnProtocol.Command] = ActorSystem(ActorSetup(), "MyActorSystem")
    implicit val ec: ExecutionContext = system.executionContext
    implicit val timeout: Timeout = Timeout(3.seconds)

    val chamador: Future[ActorRef[MensagemChamadora]] = {
      system.ask(SpawnProtocol.Spawn(behavior = CallerActor(), name = "CallerActor", props = Props.empty, _))
    }

    for (ref <- chamador) {
      ref ! MensagemSolicitarProcessamento("teste 1")
      ref ! MensagemSolicitarProcessamento("teste 2")
      ref ! MensagemSolicitarProcessamento("teste 3")
      ref ! MensagemSolicitarFinalizacao()
    }


  }

}
