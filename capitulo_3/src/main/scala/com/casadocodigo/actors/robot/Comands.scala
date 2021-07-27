package com.casadocodigo.actors.robot

import java.util.UUID


sealed trait ComandoDeMovimento

trait MoverParaFrente extends ComandoDeMovimento
trait MoverParaTras extends ComandoDeMovimento
trait MoverParaEsquerda extends ComandoDeMovimento
trait MoverParaDireita extends ComandoDeMovimento

sealed case class Coleta(UUID: UUID, peso: Double)

sealed trait ComandoDeColeta

case class Coletar(coleta: Coleta) extends ComandoDeColeta
case class Transmitir(coleta: Coleta) extends ComandoDeColeta
