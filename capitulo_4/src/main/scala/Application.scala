import akka.actor.DeadLetter
import akka.actor.typed.eventstream.EventStream.Subscribe
import akka.actor.typed.{ActorRef, ActorSystem, Props, SpawnProtocol}
import akka.util.Timeout
import com.casadocodigo.actors.{ActorSetup, CallerActor, DeadLetterActor}
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
    val atorDeadLetter: Future[ActorRef[DeadLetter]] = {
      system.ask(SpawnProtocol.Spawn(behavior = DeadLetterActor(), name = "DeadLetterActor", props = Props.empty, _))
    }

    for (ref <- chamador; refDL <- atorDeadLetter) {
      system.eventStream ! Subscribe(refDL)
      ref ! MensagemSolicitarProcessamento("teste 1")
      ref ! MensagemSolicitarProcessamento("teste 2")
      ref ! MensagemSolicitarProcessamento("teste 3")
      ref ! MensagemSolicitarFinalizacao()
      ref ! MensagemSolicitarProcessamento("teste 4")
      ref ! MensagemSolicitarProcessamento("teste 5")
      ref ! MensagemSolicitarFinalizacao()
      ref ! MensagemSolicitarProcessamento("teste 6")
      ref ! MensagemSolicitarProcessamento("teste 7")
      ref ! MensagemSolicitarFinalizacao()
      ref ! MensagemSolicitarProcessamento("teste 8")
      ref ! MensagemSolicitarProcessamento("teste 9")
      ref ! MensagemSolicitarProcessamento("teste 10")
    }
  }
}
