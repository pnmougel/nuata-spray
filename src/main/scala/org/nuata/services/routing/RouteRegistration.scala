package org.nuata.services.routing

import org.nuata.services.routing
import spray.routing
import spray.routing._

/**
 * Created by nico on 28/12/15.
 */
trait RouteRegistration extends HttpService {
  var registeredRoutes = Vector[Route]()

  def registerRoute(route: Route) = {
    registeredRoutes :+= route
  }

  def allRoutes = registeredRoutes.reduce((a, b) => { a ~ b })
}
