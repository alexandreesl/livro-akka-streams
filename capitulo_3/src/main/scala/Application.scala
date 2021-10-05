import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import com.casadocodigo.actors.APIActor.MensagemAPI
import com.casadocodigo.actors.robot.{Coleta, Coletar, CollectRobotActor, IniciarTransmissao, MoveRobotActor, MoverParaDireita, MoverParaEsquerda, MoverParaFrente, MoverParaTras}
import com.casadocodigo.actors.{APIActor, APIClassActor, ActorSetup}

import java.util.UUID
import scala.util.Random

object Application {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem(ActorSetup(), "MyActorSystem")

    exemploSimples(system)

    exemploRobo(system)

  }

  private def exemploRobo(system: ActorSystem[NotUsed]) = {
    val atorMovimentoDoRobo = system.systemActorOf(MoveRobotActor(), "MoveRobotActor")
    val atorColetorDoRobo = system.systemActorOf(CollectRobotActor(), "CollectRobotActor")
    atorMovimentoDoRobo ! MoverParaFrente
    atorMovimentoDoRobo ! MoverParaTras
    atorMovimentoDoRobo ! MoverParaEsquerda
    atorMovimentoDoRobo ! MoverParaFrente
    atorMovimentoDoRobo ! MoverParaDireita

    atorColetorDoRobo ! Coletar(Coleta(UUID.randomUUID(), Random.nextDouble()))
    atorColetorDoRobo ! Coletar(Coleta(UUID.randomUUID(), Random.nextDouble()))
    atorColetorDoRobo ! IniciarTransmissao
  }

  private def exemploSimples(system: ActorSystem[NotUsed]) = {
    val atorAPI = system.systemActorOf(APIActor(), "APIActor")

    atorAPI ! MensagemAPI(nome = "teste", documento = "123456")

    val atorClassAPI = system.systemActorOf(Behaviors.setup {
      contexto: ActorContext[MensagemAPI] =>
        new APIClassActor(contexto)
    }, "APIClassActor")

    atorClassAPI ! MensagemAPI(nome = "teste 2", documento = "123456789")
  }
}
