package org.nuata.authentication.oauth

import akka.actor.ActorRefFactory
import org.json4s.Extraction
import org.nuata.authentication.oauth.facebook.FacebookAuth
import org.nuata.authentication.oauth.github.GithubAuth
import org.nuata.authentication.oauth.google.GoogleAuth
import org.nuata.core.json.Json4sProtocol
import org.nuata.core.routing.RouteProvider
import org.nuata.users.UserRepository
import spray.client.pipelining._
import spray.http.HttpHeaders.{Accept, Authorization}
import spray.http._
import spray.httpx.encoding.{Deflate, Gzip}
import spray.routing._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by nico on 29/12/15.
 */
object OAuthRoute extends RouteProvider with Json4sProtocol {

  val authenticationProviders = Array(FacebookAuth, GoogleAuth, GithubAuth)

  def route(implicit settings: RoutingSettings, refFactory: ActorRefFactory): Route = {
    val pipeline: HttpRequest => Future[HttpResponse] = (
      addHeader(Accept(MediaTypes.`application/json`))
        ~> encode(Gzip)
        ~> sendReceive
        ~> decode(Deflate))

    val tokenPipeline: HttpRequest => Future[TokenResponse] = pipeline ~> unmarshal[TokenResponse]

    val routes = authenticationProviders.map { authProvider =>
      val route = (path("auth" / authProvider.path) & post & entity(as[OAuthCode])) { oauthCode =>
        val future = tokenPipeline(authProvider.tokenQuery(oauthCode))
          .flatMap( tokenInfo => {
            val pipe = addHeader(Authorization(OAuth2BearerToken(tokenInfo.accessToken))) ~> pipeline
            pipe(authProvider.userInfoQuery)
          }).flatMap { response =>
            val user = authProvider.getUser(response)
            UserRepository.getOrCreateUserTokenFromOAuth(user)
          }

        onSuccess(future) {
          case token: String => complete(Extraction.decompose(Map("token" -> token)))
          case _ => reject(MalformedRequestContentRejection(s"login or mail already registered"))
        }
      }
      route
    }
    routes.reduce((a, b) => { a ~ b })
  }
}
