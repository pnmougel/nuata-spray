package org.nuata.services

import spray.routing._

/**
 * Created by nico on 23/12/15.
 */

trait RouteService extends HttpService with SearchService with IndexService with LanguageDetectorService with InitService {
  val routes = {
    searchRoutes ~
      indexRoutes ~
      languageDetectorRoute ~
      initRoute
  }
}
