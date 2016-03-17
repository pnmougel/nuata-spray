package org.nuata.core.routing

import org.nuata.core.directives.CorsSupport
import org.nuata.core.reflections.Reflection
import org.reflections.Reflections
import spray.routing._
import scala.collection.JavaConversions._
import spray.routing._

/**
 * Created by nico on 23/12/15.
 */
trait RouteService
  extends TracingHttpService
  with CorsSupport {

  // Dynamically load the routes
  val routesProvided = Reflection.getInstancesOf[RouteProvider].map(_.route)

  val allRoutes = routesProvided.reduce((a, b) => { a ~ b })

  def routes(implicit settings : spray.routing.RoutingSettings, refFactory : akka.actor.ActorRefFactory) : Route = {
    cors {
      allRoutes
    }
  }
}
