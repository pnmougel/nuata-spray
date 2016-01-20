package org.nuata.authentication

import com.typesafe.config.Config
import org.nuata.authentication.Role._
import org.nuata.authentication.bearer.{BearerToken, BearerTokenHttpAuthenticator}
import org.nuata.repositories.UserRepository
import spray.http.HttpHeaders.{Authorization, `WWW-Authenticate`}
import spray.http._
import spray.routing.AuthenticationFailedRejection.{CredentialsRejected, CredentialsMissing}
import spray.routing.{RoutingSettings, AuthenticationFailedRejection, RequestContext}
import spray.routing.authentication._
import spray.routing.directives.AuthMagnet

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by nico on 29/12/15.
 */



trait Authenticator {
  def basicUserAuthenticator(implicit ec: ExecutionContext): AuthMagnet[AuthInfo] = {
    def validateUser(bearerToken: Option[BearerToken]): Future[Option[AuthInfo]] = {
//      Future(Some(new AuthInfo(org.nuata.models.User(Some("id"), None, "login", None, "email", Role.User))))

      bearerToken.map { token =>
        UserRepository.getUserByToken(token).map { optUser =>
          for(user <- optUser) yield {
            new AuthInfo(user)
          }
        }
      }.getOrElse(Future(None))
    }

    def authenticator(userPass: Option[BearerToken]): Future[Option[AuthInfo]] = validateUser(userPass)

    new BearerTokenHttpAuthenticator[AuthInfo]("Private API", authenticator _)
  }
}
