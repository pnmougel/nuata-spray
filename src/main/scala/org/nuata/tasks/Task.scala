package org.nuata.tasks

import java.util.Date

import org.nuata.tasks.TaskStatus.TaskStatus
import org.nuata.tasks.models.TaskEntry

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by nico on 17/03/16.
 */
trait Task {
  def name: String
  def description: String

  def run(options: Map[String, Any])

  var taskId : Option[String] = None

  def createTask(options: Map[String, Any]) = {
    val task = TaskEntry(None, None, name, TaskStatus.Initialize.toString, 0D, Some(new Date()), options)
    TaskRepository.index(task).map { res =>
      taskId = Some(res.id)
      run(options)
      updatePercentage(1D).map { future =>
        future.map { res =>
          updateStatus(TaskStatus.Complete)
        }
      }
    }
  }

  def update(f: TaskEntry => TaskEntry) = {
    taskId.map { id =>
      TaskRepository.update(id, f)
    }
  }

  def isStopped : Future[Boolean] = {
    taskId.map { id =>
      TaskRepository.byId(id).map { task =>
        task.isStopped
      }
    }.getOrElse(Future(false))
  }

  def updatePercentage(percentage: Double) = update(task => task.copy(percentage = percentage))
  def updateStatus(status: TaskStatus) = update(task => task.copy(status = status.toString))
  def updateDoing(doing: String) = update(task => task.copy(doing = doing))
}
