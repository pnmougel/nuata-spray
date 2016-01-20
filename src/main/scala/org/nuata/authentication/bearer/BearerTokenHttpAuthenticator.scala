package org.nuata.authentication.bearer

import spray.http.HttpHeaders.`WWW-Authenticate`
import spray.http.{HttpChallenge, HttpRequest, OAuth2BearerToken, HttpCredentials}
import spray.routing.RequestContext
import spray.routing.authentication.HttpAuthenticator

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by nico on 14/01/16.
 */

class BearerTokenHttpAuthenticator[U](val realm: String, val bearerTokenAuthenticator: Option[BearerToken] ⇒ Future[Option[U]])(implicit val executionContext: ExecutionContext)
  extends HttpAuthenticator[U] {

  def authenticate(credentials: Option[HttpCredentials], ctx: RequestContext) = {
    bearerTokenAuthenticator {
      credentials.flatMap {
        case OAuth2BearerToken(token) => Some(BearerToken(token))
        case _ => None
      }
    }
  }

  def getChallengeHeaders(httpRequest: HttpRequest) = `WWW-Authenticate`(HttpChallenge(scheme = "Bearer", realm = realm, params = Map.empty)) :: Nil
}