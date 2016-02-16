package org.nuata.services

import akka.actor.ActorRefFactory
import org.json4s.Extraction
import org.nuata.models.Unit
import org.nuata.models.queries.UserAccountQuery
import org.nuata.repositories.UserRepository
import org.nuata.services.routing.RouteProvider
import org.nuata.shared.Json4sProtocol
import spray.http.StatusCodes
import spray.routing._
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by nico on 29/12/15.
 */

object UserService extends RouteProvider with Json4sProtocol {
  def route(implicit settings: RoutingSettings, refFactory: ActorRefFactory): Route =  {
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
