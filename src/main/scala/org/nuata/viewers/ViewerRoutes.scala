package org.nuata.viewers

import akka.actor.ActorRefFactory
import org.nuata.attributes.queries.AttributeSearchQuery
import org.nuata.core.directives.GetParamsDirective._
import org.nuata.core.json.Json4sProtocol
import org.nuata.core.queries.{BaseSearchQuery, SearchQuery}
import spray.http.StatusCodes._
import spray.routing._

import scala.concurrent.ExecutionContext.Implicits.global
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.json4s.Extraction._
import org.nuata.core.routing.RouteProvider
import org.nuata.models._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Created by nico on 15/03/16.
 */
object ViewerRoutes extends RouteProvider with Json4sProtocol {
  def route(implicit settings: RoutingSettings, refFactory: ActorRefFactory): Route = {
    pathPrefix("viewer") {
      (get & getParams[BaseSearchQuery]) { searchQuery =>
        complete(ViewerRepository.list(searchQuery).map { case (nbItems, items) =>
          decompose(Map("nbItems" -> nbItems, "items" -> items))
        })
      } ~ (post & entity(as[Viewer])) { viewer =>
        complete(ViewerRepository.indexAndMap(viewer))
      } ~ (path(Segment) & delete) { id =>
        complete(ViewerRepository.deleteById(id).map { ok =>
          Map("deleted" -> ok)
        })
      } ~ (path(Segment) & put & entity(as[Viewer])) { case (id, viewer) =>
        complete(ViewerRepository.update(id, prevItem => {
          prevItem.copy(name = viewer.name, description = viewer.description)
        }).map { isOk =>
          Map("updated" -> isOk)
        })
      }
    }
  }
}
