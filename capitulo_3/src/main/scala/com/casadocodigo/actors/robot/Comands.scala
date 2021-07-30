package com.casadocodigo.actors.robot

import java.util.UUID


sealed trait ComandoDeMovimento

case class MoverParaFrente() extends ComandoDeMovimento
case class MoverParaTras() extends ComandoDeMovimento
case class MoverParaEsquerda() extends ComandoDeMovimento
case class MoverParaDireita() extends ComandoDeMovimento

sealed case class Coleta(UUID: UUID, peso: Double)

sealed trait ComandoDeColeta

case class Coletar(coleta: Coleta) extends ComandoDeColeta
case class IniciarTransmissao() extends ComandoDeColeta
case class Transmitir(coleta: Coleta)
