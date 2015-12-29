package org.nuata.authentication

import org.nuata.repositories.UserRepository
import spray.routing.authentication.{BasicAuth, UserPass}
import spray.routing.directives.AuthMagnet

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by nico on 29/12/15.
 */
trait Authenticator {
  def basicUserAuthenticator(implicit ec: ExecutionContext): AuthMagnet[AuthInfo] = {
    def validateUser(userPass: Option[UserPass]): Future[Option[AuthInfo]] = {
      userPass.map( p => {
        UserRepository.getUser(userPass.get.user).map( optUser => {
          for(user <- optUser
              if user.passwordMatches(userPass.get.pass)) yield {
            new AuthInfo(user)
          }
        })
      }).getOrElse({
        Future(None)
      })
    }

    def authenticator(userPass: Option[UserPass]): Future[Option[AuthInfo]] = validateUser(userPass)

    BasicAuth(authenticator _, realm = "Private API")
  }
}
