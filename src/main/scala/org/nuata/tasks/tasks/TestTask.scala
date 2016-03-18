package org.nuata.tasks.tasks

import org.nuata.items.ItemsRepository
import org.nuata.tasks.TaskStatus._
import org.nuata.tasks.{Task, TaskStatus}

/**
 * Created by nico on 17/03/16.
 */
class TestTask extends Task {
  val name = "test"
  val description = "A task for tests"
  var isStopped = false

  def stop = {
    isStopped = true
  }

  var status : TaskStatus = TaskStatus.Running
  var percentage = 0D
  var doing = ""

  def run(options: Map[String, Any]) = {
    status = TaskStatus.Running
    updateStatus(TaskStatus.Running)

    for(i <- 0 until 100) {
      Thread.sleep(1000)
      percentage = i.toDouble / 100
    }
    percentage = 1D


//    ItemsRepository.foreachResponse() { case (res, item, i) =>
//      if(i % 1000 == 0) {
//        updatePercentage(i.toDouble / res.totalHits)
//      }
//    }
  }
}
