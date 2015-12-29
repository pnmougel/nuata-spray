package org.nuata.repositories

import com.github.t3hnar.bcrypt._
import org.elasticsearch.action.search.SearchResponse
import org.json4s._
import org.nuata.authentication.{UserAccountQuery, Role}
import org.nuata.authentication.Role._
import org.nuata.models.User
import org.nuata.models._
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import com.sksamuel.elastic4s.ElasticDsl._
import org.nuata.shared.ElasticSearch
import spray.routing.authentication.UserPass
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by nico on 29/12/15.
 */
object UserRepository extends BaseRepository[User]("user") {
  implicit val formats = DefaultFormats

  protected def jsToInstance(jValue: JValue) = jValue.extract[User]

  def resultToEntity(res: SearchResponse) = res.as[User]

  def getUser(login: String): Future[Option[User]] = {
    client.execute {
      search in "nuata" / "user" query termQuery("login", login)
    }.map(res => {
      if(res.totalHits == 1) {
        Some(res.as[User].head)
      } else {
        None
      }
    })
  }

  def createUser(user: UserAccountQuery) : Future[Option[User]] = {
    getUser(user.login).flatMap { userOpt =>
      if(userOpt.isDefined) {
        Future(None)
      } else {
        var roles = List(Role.User)
        if(user.login == "admin") {
          roles = Role.Admin :: roles
        }
        val userAccount = User(None, None, user.login, Some(user.password.bcrypt(generateSalt)), user.email, roles)
        client.execute {
          index into "nuata" / "user" source userAccount
        }.map(res => {
          userAccount._id = Some(res.getId)
          Some(userAccount)
        })
      }
    }
  }
}
