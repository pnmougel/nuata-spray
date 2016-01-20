package org.nuata.authentication.oauth.github

import java.util.UUID

import akka.actor.ActorSystem
import org.json4s._
import org.nuata.repositories.UserRepository
import org.nuata.shared.{Json4sProtocol, Settings}
import spray.http.{HttpRequest, HttpResponse, MediaTypes, OAuth2BearerToken}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import spray.client.pipelining._
import spray.http.HttpHeaders.{Accept, Authorization}
import spray.httpx.encoding.{Deflate, Gzip}


/**
 * Created by nico on 31/12/15.
 */
case class GithubAuthentication() extends Json4sProtocol {
  implicit val system = ActorSystem()

  val conf = Settings.conf

  val pipeline: HttpRequest => Future[HttpResponse] = (
    addHeader(Accept(MediaTypes.`application/json`))
      ~> encode(Gzip)
      ~> sendReceive
      ~> decode(Deflate))

  val tokenPipeline: HttpRequest => Future[GithubToken] = pipeline ~> unmarshal[GithubToken]

  def userInfoPipeline(accessToken: String): HttpRequest => Future[GithubUserInfo] =
    (addHeader(Authorization(OAuth2BearerToken(accessToken)))
      ~> pipeline
      ~> unmarshal[GithubUserInfo])

  val apiAuthState = UUID.randomUUID().toString()

  def generateAccessToken(authCode: String, state: String): Future[Boolean] = {
    if (state == apiAuthState) {
      val clientId = conf.getString("github.clientId")
      val clientSecret = conf.getString("github.clientSecret")
      val authRequestUri = s"https://github.com/login/oauth/access_token?code=${authCode}&client_id=${clientId}&client_secret=${clientSecret}"

      tokenPipeline(Get(authRequestUri))
        .flatMap(tokenInfo => {
          userInfoPipeline(tokenInfo.access_token)(Get("https://api.github.com/user"))
        }).flatMap { userInfo =>
        println(userInfo)
        UserRepository.emailExists(userInfo.email)
      }
    } else Future.failed(new Exception("Auth States did not match.  Possibly due to CSRF."))
  }
}
