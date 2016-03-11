package org.nuata.items

import akka.actor.ActorRefFactory
import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.ElasticDsl.get
import org.json4s.jackson.JsonMethods._
import org.nuata.attributes.AttributeRepository
import org.nuata.attributes.AttributeRoutes._
import org.nuata.core.directives.GetParamsDirective._
import org.nuata.core.queries.{SuggestQuery, NameQuery}
import org.nuata.core.routing.RouteProvider
import org.nuata.items.queries.{ItemQuery}
import org.nuata.models._
import org.nuata.shared.{ElasticSearch, Json4sProtocol}
import spray.routing._

import scala.concurrent.ExecutionContext.Implicits.global



/**
 * Created by nico on 24/02/16.
 */
object ItemRoutes extends RouteProvider with Json4sProtocol {
  val repository = ItemsRepository

  def route(implicit settings: RoutingSettings, refFactory: ActorRefFactory): Route = {
    (path("item") & get & getParams[ItemQuery]) { itemQuery =>
      complete(repository.byIdOpt(itemQuery.id))
    } ~ (pathPrefix("item") & path("name") & get & getParams[NameQuery]) { query =>
      complete(repository.getNames(query))
    }
  }
}
