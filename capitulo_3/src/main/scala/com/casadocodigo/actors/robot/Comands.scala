package com.casadocodigo.actors.robot

import java.util.UUID


sealed trait ComandoDeMovimento

case object MoverParaFrente extends ComandoDeMovimento
case object MoverParaTras extends ComandoDeMovimento
case object MoverParaEsquerda extends ComandoDeMovimento
case object MoverParaDireita extends ComandoDeMovimento

sealed case class Coleta(UUID: UUID, peso: Double)

sealed trait ComandoDeColeta

case class Coletar(coleta: Coleta) extends ComandoDeColeta
case object IniciarTransmissao extends ComandoDeColeta
case class Transmitir(coleta: Coleta)
