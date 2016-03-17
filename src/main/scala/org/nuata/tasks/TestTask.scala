package org.nuata.tasks

/**
 * Created by nico on 17/03/16.
 */
class TestTask extends Task {
  val name = "test"
  val description = "A task for tests"

  def run(options: Map[String, Any]) = {
    updateStatus(TaskStatus.Running)
    for(i <- 0 until 100) {
      Thread.sleep(100)
      updatePercentage(i)
    }
    updateStatus(TaskStatus.Complete)
  }
}
