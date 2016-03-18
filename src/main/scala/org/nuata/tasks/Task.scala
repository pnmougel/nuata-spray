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

  def stop
  def percentage : Double
  def doing : String
  def status : TaskStatus

  var taskId : Option[String] = None

  def createTask(options: Map[String, Any]): Future[String] = {
    val task = TaskEntry(None, None, name, TaskStatus.Running.toString, Some(new Date()), options)
    TaskRepository.index(task).map { res =>
      taskId = Some(res.id)
      Future(run(options)).map { _ =>
        updateStatus(TaskStatus.Complete)
      }
      res.id
    }
  }

  def update(f: TaskEntry => TaskEntry) = {
    taskId.map { id =>
      TaskRepository.update(id, f)
    }
  }

  def updateStatus(status: TaskStatus) = update(task => task.copy(status = status.toString))
}
