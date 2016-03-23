package org.nuata.core.routing

import org.nuata.core.directives.CorsSupport
import spray.routing._

/**
 * This file is auto generated, do not edit
 * The generating template is located in the resources folder
 */
trait RouteService
  extends TracingHttpService
  with CorsSupport {

  val routesProvided = List(org.nuata.suggest.SuggestRoute.route, 
	org.nuata.users.UserRoute.route, 
	org.nuata.language.LanguageRoutes.route, 
	org.nuata.logging.requests.LogRequestRoutes.route, 
	org.nuata.authentication.oauth.OAuthRoute.route, 
	org.nuata.services.DatasetUpload.route,
	org.nuata.services.IconService.route, 
	org.nuata.datatypes.DataTypes.route, 
	org.nuata.viewers.ViewerRoutes.route, 
	org.nuata.tasks.TaskRoutes.route, 
	org.nuata.datatypes.DataTypesRoute.route, 
	org.nuata.items.ItemRoutes.route, 
	org.nuata.services.commonsmedia.CommonsMediaRoute.route, 
	org.nuata.attributes.AttributeRoutes.route)

  val allRoutes = routesProvided.reduce((a, b) => { a ~ b })

  def routes(implicit settings : spray.routing.RoutingSettings, refFactory : akka.actor.ActorRefFactory) : Route = {
    cors {
      allRoutes
    }
  }
}
