package org.nuata.services

import org.json4s.Extraction
import org.nuata.authentication.UserAccountQuery
import org.nuata.models.Unit
import org.nuata.repositories.UserRepository
import org.nuata.shared.Json4sProtocol
import spray.routing.HttpService
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by nico on 29/12/15.
 */

trait UserService extends HttpService with Json4sProtocol {
  val userRoutes = path("user") {
    post {
      entity(as[UserAccountQuery]) { user =>
        complete {
          UserRepository.createUser(user).map( user => {
            Extraction.decompose(user)
          })
//          Extraction.decompose(UserRepository.createUser(user))

        }
      }
    }
  }
}
