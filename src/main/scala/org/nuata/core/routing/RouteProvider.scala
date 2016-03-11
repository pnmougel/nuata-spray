package org.nuata.core.routing

import spray.routing._

/**
 * Created by nico on 16/02/16.
 */
trait RouteProvider extends TracingHttpService {
  def route(implicit settings : spray.routing.RoutingSettings, refFactory : akka.actor.ActorRefFactory) : Route
}
