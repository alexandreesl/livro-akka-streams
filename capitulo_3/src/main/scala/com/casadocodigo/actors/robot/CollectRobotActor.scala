package com.casadocodigo.actors.robot

import scala.collection.mutable._
import scala.collection.mutable.synchronized

object CollectRobotActor {

  val syncSet = new HashSet[Int] with SynchronizedSet[Int]


}
