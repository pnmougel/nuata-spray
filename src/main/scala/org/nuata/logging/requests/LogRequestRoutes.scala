package org.nuata.logging.requests

import akka.actor.ActorRefFactory
import org.nuata.authentication.Authenticator
import org.nuata.core.directives.GetParamsDirective._
import org.nuata.core.json.Json4sProtocol
import org.nuata.core.routing.RouteProvider
import org.nuata.logging.requests.queries.LogRequestQuery
import spray.routing._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by nico on 22/03/16.
 */
object LogRequestRoutes extends RouteProvider with Json4sProtocol with Authenticator {
  def route(implicit settings: RoutingSettings, refFactory: ActorRefFactory): Route = {
    (pathPrefix("logs" / "request") & isAdmin) { user =>
      (path("values") & get) {
        complete(LogRequestRepository.getValues)
      } ~ (get & getParams[LogRequestQuery]) { query =>
        complete(LogRequestRepository.list(query).map { case (nbItems, items) =>
          Map("nbItems" -> nbItems, "items" -> items)
        })
      } ~ (delete) { user =>
        LogRequestRepository.deleteIndex
        complete("ok")
      }
    }
  }
}
