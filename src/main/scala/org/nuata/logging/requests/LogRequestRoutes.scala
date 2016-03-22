package org.nuata.logging.requests

import akka.actor.ActorRefFactory
import org.nuata.logging.requests.queries.LogRequestQuery
import spray.http.StatusCodes._
import spray.routing._
import scala.concurrent.ExecutionContext.Implicits.global

import org.json4s.Extraction._

import org.nuata.core.routing.RouteProvider
import org.nuata.models._
import org.nuata.shared.Json4sProtocol
import org.nuata.core.directives.GetParamsDirective._

/**
 * Created by nico on 22/03/16.
 */
object LogRequestRoutes extends RouteProvider with Json4sProtocol {
  def route(implicit settings: RoutingSettings, refFactory: ActorRefFactory): Route = {
    pathPrefix("logs" / "request") {
      (path("values") & get) {
        complete(LogRequestRepository.getValues)
      } ~ (get & getParams[LogRequestQuery]) { query =>
        complete(LogRequestRepository.list(query).map { case (nbItems, items) =>
          Map("nbItems" -> nbItems, "items" -> items)
        })
      } ~ (delete) {
        LogRequestRepository.deleteIndex
//        LogRequestRepository.deleteAll()
        complete("ok")
      }
    }
  }
}
