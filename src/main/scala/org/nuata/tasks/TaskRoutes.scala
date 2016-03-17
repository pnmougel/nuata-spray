package org.nuata.tasks

import akka.actor.ActorRefFactory
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.json4s.Extraction._
import org.json4s.jackson.JsonMethods._
import org.nuata.attributes.queries.{AttributeSearchQuery, AttributeQuery}
import org.nuata.core.queries.{SearchQuery, SuggestQuery, NameQuery}
import org.nuata.core.reflections.Reflection
import org.nuata.core.routing.RouteProvider
import org.nuata.items.queries.ItemQuery
import org.nuata.models._
import org.nuata.core.directives.GetParamsDirective._
import org.nuata.shared.{ElasticSearch, Json4sProtocol}
import org.nuata.tasks.queries.TaskQuery
import spray.http.StatusCodes._
import spray.routing._
import scala.concurrent.ExecutionContext.Implicits.global

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.ElasticDsl._


/**
 * Created by nico on 17/03/16.
 */
object TaskRoutes extends RouteProvider with Json4sProtocol {
  val tasks = Reflection.getInstancesOf[Task]
  val taskNameToTask = Map(tasks.map(task => (task.name.toLowerCase, task)) :_*)

  def route(implicit settings: RoutingSettings, refFactory: ActorRefFactory): Route = {
    pathPrefix("task") {
      (path("names") & get) {
        val result = tasks.map(task => Map("name" -> task.name, "description" -> task.description))
        complete(result)
      } ~ (path("stop" / Segment) & post) { id =>
        complete(TaskRepository.update(id, task => {
          task.copy(isStopped = true)
        }))
      } ~ (path("run" / Segment) & post & entity(as[Map[String, Any]])) { case (taskName, taskOptions) =>
        val taskNameLower = taskName.toLowerCase
        val future = taskNameToTask.get(taskNameLower).map { task =>
          Reflection.newInstanceOf[Task](task.getClass).map { newTask =>
            newTask.createTask(taskOptions)
            Map("running" -> true)
          }.getOrElse(Map("error" -> "Unable to create the task"))
        }.getOrElse(Map("error" -> s"invalid task ${taskName}"))
        complete(future)
      } ~ (path(Segment) & delete) { id =>
        complete(TaskRepository.deleteById(id).map { res =>
          Map("deleted" -> res)
        })
      } ~ (path("statuses") & get) {
        complete(TaskStatus.values.map(_.toString.toLowerCase))
      } ~ (get & getParams[TaskQuery]) { searchQuery =>
        complete(TaskRepository.list(searchQuery).map { case (nbItems, items) =>
          Map("nbItems" -> nbItems, "items" -> items)
        })
      }
    }
  }
}
