package org.nuata.mock

import akka.actor.ActorRefFactory
import org.nuata.mock.generators._
import org.nuata.services.routing.RouteProvider
import org.nuata.shared.Json4sProtocol
import spray.routing.{RoutingSettings, Route}
import org.json4s.Extraction._

/**
 * Created by nico on 16/02/16.
 */
object MockRoutes extends RouteProvider with Json4sProtocol {
  def route(implicit settings : RoutingSettings, refFactory : ActorRefFactory) : Route = {
    (path("mock" / "label") & get) {
      complete(decompose(LabelGenerator.generate()))
    } ~
    (path("mock" / "item") & get) {
       complete(decompose(ItemWithIdGenerator.generate()))
    }
  }
}
