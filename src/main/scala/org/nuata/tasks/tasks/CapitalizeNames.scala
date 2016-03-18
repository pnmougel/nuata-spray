package org.nuata.tasks.tasks

import com.sksamuel.elastic4s.{RichSearchHit, RichSearchResponse}
import org.nuata.attributes.AttributeRepository
import org.nuata.core.{BaseRepository, NodeRepository}
import org.nuata.items.ItemsRepository
import org.nuata.models.{Attribute, Label}
import org.nuata.tasks.{Task, TaskStatus}
import org.nuata.tasks.TaskStatus._
import scala.concurrent.duration._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by nico on 18/03/16.
  */
class CapitalizeNames extends Task {
  val name = "Capitalize attribute names"
  val description = "Capitalize the names of the attributes"

  var isStopped = false

  def stop = {
    isStopped = true
  }

  var status: TaskStatus = TaskStatus.Running
  var percentage = 0D
  var doing = ""

  def cleanName(name: String) : String = {
    if (name.length > 1 && name.charAt(0).isLower && name.charAt(1).isLower) {
      name.capitalize
    } else {
      name
    }.replaceAllLiterally("Wikidata property", "Property")
  }

  def run(options: Map[String, Any]) = {
    doing = "Cleaning attribute names"
    AttributeRepository.allIds { case (res, hit, i) =>
      Await.result(AttributeRepository.update(hit.id, item => {
        val newLabels = for ((lang, label) <- item.labels) yield {
          (lang, label.copy(name = label.name.map(cleanName)))
        }
        item.copy(labels = newLabels)
      }), 1 minutes)
      percentage = i.toDouble / res.totalHits
      println(percentage)
    }
    doing = "Cleaning item names"
    ItemsRepository.allIds { case (res, hit, i) =>
      Await.result(ItemsRepository.update(hit.id, item => {
        val newLabels = for ((lang, label) <- item.labels) yield {
          (lang, label.copy(name = label.name.map(cleanName)))
        }
        item.copy(labels = newLabels)
      }), 1 minutes)
      if(i % 1000 == 0) {
        percentage = i.toDouble / res.totalHits
        println(percentage)
      }

    }
    println("Done")
    status = TaskStatus.Complete
    percentage = 1D
  }
}
