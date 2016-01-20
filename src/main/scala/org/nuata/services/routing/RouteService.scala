package org.nuata.services

import org.nuata.directives.CorsSupport
import org.nuata.services.routing.RouteRegistration
import spray.routing._

/**
 * Created by nico on 23/12/15.
 */
trait RouteService
  extends HttpService
  with RouteRegistration
  with SearchService
  with IndexService
  with LanguageDetectorService
  with InitService
  with StatsService
  with UserService
  with CorsSupport
  with OauthService
  with WikiDataService
  with DatasetUpload
  with IconService
{

  val routes = {
    cors {
      allRoutes
    }
  }
}
