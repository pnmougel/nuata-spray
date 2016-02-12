package org.nuata.directives

import spray.routing.directives.RouteDirectives._
import spray.routing.directives.MethodDirectives._
import spray.routing.directives.HeaderDirectives._
import spray.routing.directives.BasicDirectives._
import spray.routing.directives.RespondWithDirectives._
import spray.http._
import spray.http.HttpMethods._
import spray.http.HttpHeaders._
import spray.routing.{Route, MethodRejection, RejectionHandler, Directive0}
import spray.routing._


trait CorsSupport extends HttpService {
  lazy val allowedOrigin: AllowedOrigins = {
//    val config = ConfigFactory.load()
    // val sAllowedOrigin = config.getString("cors.allowed-origin")
//    val sAllowedOrigin = "http://localhost:4000"
    val sAllowedOrigin = Seq("http://localhost:4000")
    SomeOrigins(sAllowedOrigin.map(x => HttpOrigin(x)))
//      Seq(HttpOrigin(sAllowedOrigin)))
  }

  lazy val origin2 = SomeOrigins(Seq(HttpOrigin("https://nuata.org")))

  //this directive adds access control headers to normal responses
  private def addAccessControlHeaders: Directive0 = {
    mapHttpResponseHeaders { headers =>
      `Access-Control-Allow-Origin`(allowedOrigin) +:
//        `Access-Control-Allow-Origin`(origin2) +:
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
    )
    )
  }

  def cors(r: Route) = addAccessControlHeaders {
    preflightRequestHandler ~ r
  }
}
