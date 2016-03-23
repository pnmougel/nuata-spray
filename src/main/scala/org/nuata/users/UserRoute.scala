package org.nuata.users

import akka.actor.ActorRefFactory
import org.nuata.authentication.{AuthInfo, Authenticator, Permission}
import org.nuata.core.json.Json4sProtocol
import org.nuata.core.routing.RouteProvider
import org.nuata.core.settings.Settings
import org.nuata.users.queries.AdminAccountQuery
import spray.routing._
import org.nuata.core.directives.GetParamsDirective._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by nico on 29/12/15.
 */

object UserRoute extends RouteProvider with Json4sProtocol with Authenticator {

  def route(implicit settings: RoutingSettings, refFactory: ActorRefFactory): Route =  {
    pathPrefix("user") {
      (path("admin") & get & getParams[AdminAccountQuery]) { user =>
        UserRepository.createIndex
//        UserRepository.createIndex
        val future: Future[String] = UserRepository.getUserByEmailAndPassword(user.email, user.password).flatMap { userOpt =>
          userOpt.map { user =>
            Future(user.token.get)
          }.getOrElse {
            val defaultAdminMail = Settings.getString("admin.email")
            val defaultAdminPassword = Settings.getString("admin.password")
            if(user.email == defaultAdminMail && user.password == defaultAdminPassword) {
              UserRepository.createDefaultAdminAccount(user.email, user.password)
            } else {
              Future("")
            }
          }
        }
        complete(future.map { token =>
          if(token.isEmpty) {
            Map("error" -> "Invalid authentication")
          } else {
            Map("token" -> token)
          }
        })
      } ~ (get & isAdmin) { user =>
        println(user.user)
        complete("hello admin")
      }
      //    post {
      //      entity(as[UserAccountQuery]) { user =>
      //        onSuccess(UserRepository.createUser(user)) {
      //          case None => reject(MalformedRequestContentRejection(s"login or mail already registered"))
      //          case Some(userId) => complete(Extraction.decompose(Map("id" -> userId)))
      //        }
      //      }
      //    } ~
//      (delete) {
//        UserRepository.createIndex
//        complete("ok")
//      }
    }
  }
}
