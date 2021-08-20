package com.casadocodigo.stress.simulations

trait UtilitarioDeNumeros {

  val leftLimit = 1L
  val rightLimit = 100L

  def generateLong: Long = leftLimit + (Math.random * (rightLimit - leftLimit)).toLong

}
