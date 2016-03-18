package org.nuata.tasks.tasks

import org.nuata.attributes.AttributeRepository
import org.nuata.items.ItemsRepository
import org.nuata.tasks.{TaskStatus, Task}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by nico on 18/03/16.
 */
class CountItemPerAttributes  extends Task {
  val name = "Count items per attribute"
  val description = "Count the number of items having an attribute"

  def run(options: Map[String, Any]) = {
    updateStatus(TaskStatus.Running)
    AttributeRepository.foreach() { case (res, item, i) =>
      if(item.nbItems.isEmpty) {
        ItemsRepository.countItemsWithAttribute(item._id.get).map { nbItems =>
          AttributeRepository.update(item._id.get, attribute => attribute.copy(nbItems = Some(nbItems.toInt)))
        }
      }
      if(i % 100 == 0) {
        updatePercentage(i.toDouble / res.totalHits)
      }
    }
  }
}
