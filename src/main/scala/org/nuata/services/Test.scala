package org.nuata.services

import akka.actor.ActorRefFactory
import org.nuata.services.routing.RouteProvider
import org.nuata.shared.Json4sProtocol
import spray.http.StatusCodes
import spray.routing.PathMatchers.Segment
import spray.routing._
//import kamon.spray.NameGenerator
//import kamon.spray._
//import kamon.spray.DefaultNameGenerator
//import kamon.spray.KamonTraceDirectives._


/**
 * Created by nico on 08/03/16.
 */
object Test extends RouteProvider with Json4sProtocol {
  def route(implicit settings: RoutingSettings, refFactory: ActorRefFactory): Route =  {
    pathPrefix("hello") {
      get {
        complete("Ok")

      }
    }
  }
}
