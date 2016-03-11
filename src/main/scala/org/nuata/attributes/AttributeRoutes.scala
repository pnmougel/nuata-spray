package org.nuata.attributes

import akka.actor.ActorRefFactory
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.json4s.Extraction._
import org.json4s.jackson.JsonMethods._
import org.nuata.attributes.queries.{AttributeSearchQuery, AttributeQuery}
import org.nuata.core.queries.{SuggestQuery, NameQuery}
import org.nuata.core.routing.RouteProvider
import org.nuata.items.queries.ItemQuery
import org.nuata.models._
import org.nuata.core.directives.GetParamsDirective._
import org.nuata.shared.{ElasticSearch, Json4sProtocol}
import spray.http.StatusCodes._
import spray.routing._
import scala.concurrent.ExecutionContext.Implicits.global

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.ElasticDsl._


object AttributeRoutes extends RouteProvider with Json4sProtocol {
  val repository = AttributeRepository

  def route(implicit settings: RoutingSettings, refFactory: ActorRefFactory): Route = {
    (pathPrefix("attribute" / Segment) & path("visible" / IntNumber) & post) { (id, visibility) =>
      complete(repository.update(id, item => {
        item
//        item.copy(visible = visibility != 0)
      }))
    } ~ (pathPrefix("attribute") & path("search") & get & getParams[AttributeSearchQuery]) { query =>
      complete(repository.attributeSearch(query).map { case (nbItems, items) =>
        decompose(Map("nbItems" -> nbItems, "items" -> items))
      })
    } ~ (pathPrefix("attribute") & path("suggest") & get & getParams[SuggestQuery]) { query =>
      complete(repository.getSuggestions(query))
    } ~ (pathPrefix("attribute") & path("name") & get & getParams[NameQuery]) { query =>
      complete(repository.getNames(query))
    } ~ (pathPrefix("attribute") & get & getParams[AttributeQuery]) { query =>
      complete(repository.byIds(query.id))
    }
  }
}
