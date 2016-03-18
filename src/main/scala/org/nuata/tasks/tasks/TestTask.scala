package org.nuata.tasks.tasks

import org.nuata.items.ItemsRepository
import org.nuata.tasks.{Task, TaskStatus}

/**
 * Created by nico on 17/03/16.
 */
class TestTask extends Task {
  val name = "test"
  val description = "A task for tests"

  def run(options: Map[String, Any]) = {
    updateStatus(TaskStatus.Running)
    ItemsRepository.foreach() { case (res, item, i) =>
      res.totalHits
      if(i % 1000 == 0) {
        updatePercentage(i.toDouble / res.totalHits)
      }
    }
  }
}
