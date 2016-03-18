package org.nuata.tasks

import akka.actor.ActorRefFactory
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.json4s.Extraction._
import org.json4s.jackson.JsonMethods._
import org.nuata.attributes.queries.{AttributeQuery, AttributeSearchQuery}
import org.nuata.core.queries.{IdQuery, NameQuery, SearchQuery, SuggestQuery}
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

import scala.collection.mutable


/**
 * Created by nico on 17/03/16.
 */
object TaskRoutes extends RouteProvider with Json4sProtocol {
  val tasks = Reflection.getInstancesOf[Task]
  val taskNameToTask = Map(tasks.map(task => (task.name.toLowerCase, task)) :_*)

  var runningTaskIds = mutable.HashMap[String, Task]()

  def route(implicit settings: RoutingSettings, refFactory: ActorRefFactory): Route = {
    pathPrefix("task") {
      (path("names") & get) {
        val result = tasks.map(task => Map("name" -> task.name, "description" -> task.description))
        complete(result)
      } ~ (path("stop" / Segment) & post) { id =>
        runningTaskIds.get(id).map { task =>
          task.stop
        }
        complete(Map("stopped" -> true))
      } ~ (path("run" / Segment) & post & entity(as[Map[String, Any]])) { case (taskName, taskOptions) =>
        val taskNameLower = taskName.toLowerCase

        val future = taskNameToTask.get(taskNameLower).map { task =>
          Reflection.newInstanceOf[Task](task.getClass).map { newTask =>
            newTask.createTask(taskOptions).map { id =>
              TaskRoutes.runningTaskIds(id) = newTask
            }
            Map("running" -> true)
          }.getOrElse(Map("error" -> "Unable to create the task"))
        }.getOrElse(Map("error" -> s"invalid task ${taskName}"))
        complete(future)
      } ~ (delete & getParams[IdQuery]) { idQuery =>
        complete(TaskRepository.deleteById(idQuery.id).map { res =>
          Map("deleted" -> res)
        })
      } ~ (path("statuses") & get) {
        complete(TaskStatus.values.map(_.toString.toLowerCase))
      } ~ (path("state") & get & getParams[IdQuery]) { idQuery =>
          val foo = runningTaskIds.get(idQuery.id).map { task =>
            Map("running" -> true, "percentage" -> task.percentage, "doing" -> task.doing, "status" -> task.status)
          }.getOrElse {
            TaskRepository.update(idQuery.id, _.copy(status = TaskStatus.Error.toString));
            Map("running" -> false, "status" -> "error")
          }
          complete( foo)
      } ~ (get & getParams[TaskQuery]) { searchQuery =>
        complete(TaskRepository.list(searchQuery).map { case (nbItems, items) =>
          Map("nbItems" -> nbItems, "items" -> items)
        })
      }
    }
  }
}
