package org.nuata.suggest

import akka.actor.ActorRefFactory
import org.nuata.attributes.AttributeRoutes._
import org.nuata.core.directives.GetParamsDirective._
import org.nuata.core.json.Json4sProtocol
import org.nuata.core.queries.SuggestQuery
import org.nuata.items.ItemsRepository
import spray.http.StatusCodes._
import spray.routing._
import scala.concurrent.ExecutionContext.Implicits.global

import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.json4s.Extraction._

import org.nuata.core.routing.RouteProvider
import org.nuata.models._

/**
 * Created by nico on 11/03/16.
 */
object SuggestRoute extends RouteProvider with Json4sProtocol {
  def route(implicit settings: RoutingSettings, refFactory: ActorRefFactory): Route = {
    (path("suggest") & get & getParams[SuggestQuery]) { query =>
      complete(ItemsRepository.getSuggestions(query))
    }
  }
}
