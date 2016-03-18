package org.nuata.datatypes

import com.sksamuel.elastic4s.ElasticDsl._
import org.json4s.Extraction._
import org.nuata.services.routing.RouteProvider
import org.nuata.shared.{Json4sProtocol, ElasticSearch}
import spray.routing._


object DataTypes extends RouteProvider with Json4sProtocol {
  val client = ElasticSearch.client

  def route(implicit settings: spray.routing.RoutingSettings, refFactory: akka.actor.ActorRefFactory) = {
    (pathPrefix("datatypes") & get) {
      val dataTypes = Array("text", "url", "itemRef", "attributeRef", "commonsMedia", "coordinate", "externalId", "localizedText", "math", "quantity", "time")
      complete(dataTypes)
    }
  }
}
