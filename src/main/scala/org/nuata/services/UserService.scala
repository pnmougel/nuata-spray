package org.nuata.services

import org.json4s.Extraction
import org.nuata.models.Unit
import org.nuata.models.queries.UserAccountQuery
import org.nuata.repositories.UserRepository
import org.nuata.services.routing.RouteRegistration
import org.nuata.shared.Json4sProtocol
import spray.http.StatusCodes
import spray.routing.{MalformedRequestContentRejection, AuthenticationFailedRejection, HttpService}
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by nico on 29/12/15.
 */

trait UserService extends RouteRegistration with Json4sProtocol {
  registerRoute {
    pathPrefix("user") {
      //    post {
      //      entity(as[UserAccountQuery]) { user =>
      //        onSuccess(UserRepository.createUser(user)) {
      //          case None => reject(MalformedRequestContentRejection(s"login or mail already registered"))
      //          case Some(userId) => complete(Extraction.decompose(Map("id" -> userId)))
      //        }
      //      }
      //    } ~
      delete {
        path(Segment) { id =>
          onSuccess(UserRepository.deleteById(id)) {
            case false => reject(MalformedRequestContentRejection(s"user id ${id} not found"))
            case true => complete(StatusCodes.OK)
          }
        }
      }
    }
  }
}
