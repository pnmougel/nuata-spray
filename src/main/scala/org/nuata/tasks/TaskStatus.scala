package org.nuata.tasks

/**
 * Created by nico on 17/03/16.
 */
object TaskStatus extends Enumeration {
  type TaskStatus = Value
  val Initialize, Running, Error, Complete, Stop = Value
}
