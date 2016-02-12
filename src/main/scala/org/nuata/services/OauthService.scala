package org.nuata.services

import org.json4s.Extraction
import org.nuata.authentication.oauth.{GoogleUserInfo, TokenResponse}
import org.nuata.authentication.oauth.facebook.{FacebookUserInfo, FacebookToken}
import org.nuata.authentication.oauth.github.{GithubCode, GithubToken, GithubUserInfo}
import org.nuata.repositories.UserRepository
import org.nuata.services.routing.RouteRegistration
import org.nuata.shared.{Json4sProtocol, Settings}
import spray.client.pipelining._
import spray.http.HttpHeaders.{Accept, Authorization}
import spray.http._
import spray.httpx.encoding.{Deflate, Gzip}
import spray.routing.{HttpService, MalformedRequestContentRejection}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by nico on 29/12/15.
 */
trait OauthService extends RouteRegistration with Json4sProtocol {
  val pipeline: HttpRequest => Future[HttpResponse] = (
    addHeader(Accept(MediaTypes.`application/json`))
      ~> encode(Gzip)
      ~> sendReceive
      ~> decode(Deflate))

  val tokenPipeline: HttpRequest => Future[TokenResponse] = pipeline ~> unmarshal[TokenResponse]
  val facebookTokenPipeline: HttpRequest => Future[FacebookToken] = pipeline ~> unmarshal[FacebookToken]

  def userInfoPipeline(accessToken: String): HttpRequest => Future[GithubUserInfo] =
    (addHeader(Authorization(OAuth2BearerToken(accessToken)))
      ~> pipeline
      ~> unmarshal[GithubUserInfo])

  def facebookUserInfoPipeline(accessToken: String): HttpRequest => Future[FacebookUserInfo] =
    (addHeader(Authorization(OAuth2BearerToken(accessToken)))
      ~> pipeline
      ~> unmarshal[FacebookUserInfo])

  def googleUserInfoPipeline(accessToken: String): HttpRequest => Future[GoogleUserInfo] =
    (addHeader(Authorization(OAuth2BearerToken(accessToken)))
      ~> pipeline
      ~> unmarshal[GoogleUserInfo])

  registerRoute {
    (path("auth" / "github") & post & entity(as[GithubCode]) ) { rq =>
      val clientId = Settings.getString("github.clientId")
      val clientSecret = Settings.getString("github.clientSecret")

      val future = tokenPipeline(Get(s"https://github.com/login/oauth/access_token?code=${rq.code}&client_id=${clientId}&client_secret=${clientSecret}"))
        .flatMap( tokenInfo => {
          userInfoPipeline(tokenInfo.access_token)(Get("https://api.github.com/user"))
        }).flatMap { userInfo =>
          UserRepository.createUser(userInfo.email, Some(userInfo.name))
        }

      onSuccess(future) {
        case token: String => complete(Extraction.decompose(Map("token" -> token)))
        case _ => reject(MalformedRequestContentRejection(s"login or mail already registered"))
      }
    }
  }

  registerRoute {
    (path("auth" / "facebook") & post & entity(as[GithubCode]) ) { rq =>
      val clientId = Settings.getString("facebook.clientId")
      val clientSecret = Settings.getString("facebook.clientSecret")
      val apiVersion = Settings.getString("facebook.apiVersion")
      val redirectUri = Settings.getString("facebook.redirectUri")

      val tokenUrl = s"https://graph.facebook.com/${apiVersion}/oauth/access_token?client_id=${clientId}&redirect_uri=${redirectUri}&client_secret=${clientSecret}&code=${rq.code}"
      val future = tokenPipeline(Get(tokenUrl))
        .flatMap( tokenInfo => {
          facebookUserInfoPipeline(tokenInfo.access_token)(Get(s"https://graph.facebook.com/${apiVersion}/me?fields=id,name,email,verified"))
        }).flatMap { userInfo =>
          UserRepository.createUser(userInfo.email, Some(userInfo.name))
        }

      onSuccess(future) {
        case token: String => complete(Extraction.decompose(Map("token" -> token)))
        case _ => reject(MalformedRequestContentRejection(s"login or mail already registered"))
      }
    }
  }

  registerRoute {
    (path("auth" / "google") & post & entity(as[GithubCode]) ) { rq =>
      val clientId = Settings.getString("google.clientId")
      val clientSecret = Settings.getString("google.clientSecret")
      val apiVersion = Settings.getString("google.apiVersion")

       val tokenUrl = s"https://www.googleapis.com/oauth2/${apiVersion}/token?client_id=${clientId}&redirect_uri=${rq.redirectUri}&client_secret=${clientSecret}&code=${rq.code}&grant_type=authorization_code"

      val future = tokenPipeline(Post(tokenUrl))
        .flatMap( tokenInfo => {
          googleUserInfoPipeline(tokenInfo.access_token)(Get("https://www.googleapis.com/plus/v1/people/me"))
        }).flatMap { userInfo =>
          UserRepository.createUser(userInfo.emails.head.value, userInfo.displayName)
        }
      onSuccess(future) {
        case token: String => complete(Extraction.decompose(Map("token" -> token)))
        case _ => reject(MalformedRequestContentRejection(s"login or mail already registered"))
      }
    }
  }
}
