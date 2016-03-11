package org.nuata.services

import akka.actor.ActorRefFactory
import org.json4s.Extraction
import org.nuata.authentication.queries.UserAccountQuery
import org.nuata.core.directives.CorsSupport
import org.nuata.core.routing.RouteProvider
import org.nuata.models._
import org.nuata.shared.Json4sProtocol
import spray.http.StatusCodes._
import spray.routing._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by nico on 30/12/15.
 */
object StatsService extends RouteProvider with Json4sProtocol {
  def route(implicit settings: RoutingSettings, refFactory: ActorRefFactory): Route =  {
    path("stats") {
      get {
        complete {
//          for(facts <- FactRepository.count;
//              dimensions <- DimensionRepository.count;
//              categories <- CategoryRepository.count;
//              oois <- OoiRepository.count;
//              sources <- SourceRepository.count;
//              users <- UserRepository.count) yield {
//            Extraction.decompose(Map(
//              "facts" -> facts.getCount,
//              "dimensions" -> dimensions.getCount,
//              "categories" -> categories.getCount,
//              "oois" -> oois.getCount,
//              "sources" -> sources.getCount,
//              "users" -> users.getCount
//            ))
//          }
          "ok"
        }
      }
    }
  }
}
