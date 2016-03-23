package org.nuata.core.directives

import spray.http.HttpHeaders._
import spray.http.HttpMethods._
import spray.http._
import spray.routing.{Directive0, Route, _}


trait CorsSupport extends HttpService {
  lazy val allowedOrigin: AllowedOrigins = {
    AllOrigins
  }

  //this directive adds access control headers to normal responses
  private def addAccessControlHeaders: Directive0 = {
    mapHttpResponseHeaders { headers =>
      `Access-Control-Allow-Origin`(allowedOrigin) +:
        `Access-Control-Allow-Credentials`(true) +:
        `Access-Control-Allow-Headers`("Authorization", "Content-Type", "X-Requested-With") +:
        headers
    }
  }

  //this handles preflight OPTIONS requests. TODO: see if can be done with rejection handler,
  //otherwise has to be under addAccessControlHeaders
  private def preflightRequestHandler: Route = options {
    complete(HttpResponse(200).withHeaders(
      `Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)
    ))
  }

  def cors(r: Route) = addAccessControlHeaders {
    preflightRequestHandler ~ r
  }
}
