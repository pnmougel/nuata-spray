package org.nuata.tasks.tasks

import org.nuata.attributes.AttributeRepository
import org.nuata.items.ItemsRepository
import org.nuata.models.Attribute
import org.nuata.tasks.TaskStatus.TaskStatus
import org.nuata.tasks.{Task, TaskStatus}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by nico on 18/03/16.
  */
class CountItemPerAttributes extends Task {
  val name = "Count items per attribute"
  val description = "Count the number of items having an attribute"

  var isStopped = false

  def stop = {
    isStopped = true
  }

  var status: TaskStatus = TaskStatus.Initialize
  var percentage = 0D
  var doing = ""

  def run(options: Map[String, Any]) = {
    status = TaskStatus.Running
    AttributeRepository.all { case (res, item: Attribute, i) =>
      if (item.nbItems.isEmpty) {
        ItemsRepository.countItemsWithAttribute(item._id.get).map { nbItems =>
          AttributeRepository.update(item._id.get, _.copy(nbItems = Some(nbItems.toInt)))
        }
      }
      percentage = i.toDouble / res.totalHits
    }
    status = TaskStatus.Complete
    percentage = 1D
  }
}
