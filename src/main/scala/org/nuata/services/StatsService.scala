package org.nuata.services

import org.json4s.Extraction
import org.nuata.directives.CorsSupport
import org.nuata.models._
import org.nuata.models.queries.UserAccountQuery
import org.nuata.repositories._
import org.nuata.services.routing.RouteRegistration
import org.nuata.shared.Json4sProtocol
import spray.http.StatusCodes._
import spray.routing._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by nico on 30/12/15.
 */
trait StatsService extends RouteRegistration with Json4sProtocol {
  registerRoute {
    path("stats") {
      get {
        complete {
          for(facts <- FactRepository.count;
              dimensions <- DimensionRepository.count;
              categories <- CategoryRepository.count;
              oois <- OoiRepository.count;
              sources <- SourceRepository.count;
              users <- UserRepository.count) yield {
            Extraction.decompose(Map(
              "facts" -> facts.getCount,
              "dimensions" -> dimensions.getCount,
              "categories" -> categories.getCount,
              //              "oois" -> oois.getCount,
              "sources" -> sources.getCount,
              "users" -> users.getCount
            ))
          }
        }
      }
    }
  }
}