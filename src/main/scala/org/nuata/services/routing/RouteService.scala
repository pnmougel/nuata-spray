package org.nuata.services

import org.nuata.directives.CorsSupport
import org.nuata.services.routing.RouteProvider
import org.reflections.Reflections
import spray.routing._
import scala.collection.JavaConversions._
import spray.routing._

/**
 * Created by nico on 23/12/15.
 */
trait RouteService
  extends HttpService
  with CorsSupport {

  // Dynamically load the routes
  val reflections = new Reflections("org.nuata")
  val subTypes = reflections.getSubTypesOf(classOf[org.nuata.services.routing.RouteProvider])
  val routesProvided =
    (for(subType <- subTypes;
      constructor <- subType.getDeclaredConstructors
      if constructor.getParameterCount == 0
  ) yield {
    constructor.setAccessible(true)
    val obj = constructor.newInstance().asInstanceOf[RouteProvider]
    obj.route
  }).reduce((a, b) => { a ~ b })

  val routes = {
    cors {
      routesProvided
    }
  }
}
