package org.nuata.datatypes

import akka.actor.ActorRefFactory
import org.nuata.core.json.Json4sProtocol
import org.nuata.core.routing.RouteProvider
import spray.routing.{Route, RoutingSettings}

/**
 * Created by nico on 15/03/16.
 */
object DataTypesRoute extends RouteProvider with Json4sProtocol {
  val dataTypes = Array("attributeRef", "commonsMedia", "externalId", "itemRef", "localizedText", "math", "quantity", "text", "time", "url", "coordinate")
  def route(implicit settings: RoutingSettings, refFactory: ActorRefFactory): Route = {
    (path("datatypes") & get) {
      complete(dataTypes)
    }
  }
}
