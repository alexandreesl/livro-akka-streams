package com.casadocodigo.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.casadocodigo.actors.DBActor.MensagemBanco

object APIActor {

  case class MensagemAPI(nome: String, documento: String)

  def apply(): Behavior[MensagemAPI] = Behaviors.setup { contexto =>

    val refAtorDB = contexto.spawn(DBActor(), "DBActor")

    Behaviors.receive {
      (contexto, mensagem) =>
        contexto.log.info(s"mensagem $mensagem recebida e enviada para a API!")
        refAtorDB ! MensagemBanco(nome = mensagem.nome, documento = mensagem.documento)
        Behaviors.same
    }
  }

}
