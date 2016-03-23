package org.nuata.authentication

import org.nuata.authentication.bearer.{BearerToken, BearerTokenHttpAuthenticator}
import org.nuata.users.UserRepository
import org.nuata.users.UserRoute._
import spray.routing._
import spray.routing.directives.AuthMagnet

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by nico on 29/12/15.
 */
trait Authenticator {
  val isAdmin: Directive1[AuthInfo] = {
    authenticate(basicUserAuthenticator).flatMap {
      case a: AuthInfo => {
        if(a.hasPermission(Permission.Admin)) {
          provide(a)
        } else {
          reject
        }
      }
      case _ => {
        reject
      }
    }
  }

  def basicUserAuthenticator(implicit ec: ExecutionContext): AuthMagnet[AuthInfo] = {
    def validateUser(bearerToken: Option[BearerToken]): Future[Option[AuthInfo]] = {
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

//  def authenticate
}
