package org.nuata.datatypes

import org.nuata.core.ElasticSearch
import org.nuata.core.json.Json4sProtocol
import org.nuata.core.routing.RouteProvider


object DataTypes extends RouteProvider with Json4sProtocol {
  val client = ElasticSearch.client

  def route(implicit settings: spray.routing.RoutingSettings, refFactory: akka.actor.ActorRefFactory) = {
    (pathPrefix("datatypes") & get) {
      val dataTypes = Array("text", "url", "itemRef", "attributeRef", "commonsMedia", "coordinate", "externalId", "localizedText", "math", "quantity", "time")
      complete(dataTypes)
    }
  }
}
