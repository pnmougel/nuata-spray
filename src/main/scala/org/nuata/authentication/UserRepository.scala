package org.nuata.authentication

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.jackson.ElasticJackson
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.nuata.authentication.bearer.BearerToken
import org.nuata.core.BaseRepository
import org.nuata.models.User

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random


/**
 * Created by nico on 29/12/15.
 */
object UserRepository extends BaseRepository[User]("user") {
  private val maxTryTokenGenerate = 10

  /**
   * Generate a new user token and ensures that the token does not already exists
   * It is very very very unlikely to occur, so the case where two query are sent with concurrent
   * token generation leading to the same token is not handled.
   * @param nbTry
   * @return
   */
  def generateToken(nbTry : Int = 0) : Future[String] = {
    if(nbTry > maxTryTokenGenerate) {
      // TODO: Create a valid Exception
      Future.failed(new ArithmeticException())
    } else {
      val token = Random.alphanumeric.take(64).mkString
      client.execute {
        search in path query termQuery("token", token)
      }.flatMap { res =>
        if(res.getHits.getTotalHits > 0) {
          generateToken()
        } else {
          Future(token)
        }
      }
    }
  }

  def generateTokenForUser(userId: String) : Future[String] = {
    generateToken().flatMap { token =>
      client.execute {
        ElasticDsl.update id userId in path doc(
          "token" -> token,
          "token_created_at" -> new java.util.Date())
      }.map(res => {
        token
      })
    }
  }

  def getUserByToken(token: BearerToken): Future[Option[User]] = {
    client.execute {
      search in path query termQuery("token", token)
    }.map(res => {
      if(res.totalHits == 1) {
        Some(res.as[User].head)
      } else {
        None
      }
    })
  }

  def createUser(email: String, name: Option[String]) = {
    val userAccount = User(None, None, email, name, Role.User)
    UserRepository.index(userAccount).flatMap(res => {
      generateTokenForUser(res.getId)
    })
  }

  def getUser(login: String): Future[Option[User]] = {
    client.execute {
      search in path query termQuery("login", login)
    }.map(res => {
      if(res.totalHits == 1) {
        Some(res.as[User].head)
      } else {
        None
      }
    })
  }

  def emailExists(email: String) : Future[Boolean] = {
    client.execute { search in path query termQuery("email", email) }
      .map(res => res.totalHits != 0)
  }

  def accountExists(login: String, email: String) : Future[Boolean] = {
    client.execute {
      search in path query bool { should {
        Seq(termQuery("login", login), termQuery("email", email))
      }}
    }.map(res => {
      res.totalHits != 0
    })
  }

//  def createUser(user: UserAccountQuery) : Future[Option[String]] = {
//    accountExists(user.login, user.email).flatMap { userExists =>
//      if(userExists) {
//        Future(None)
//      } else {
//        var roles = List(Role.User)
//        if(user.login == "admin") {
//          roles = Role.Admin :: roles
//        }
//        val userAccount = User(None, None, user.login, Some(user.password.bcrypt(generateSalt)), user.email, Role.User)
//        client.execute {
//          index into "nuata" / "user" source userAccount
//        }.map(res => {
//          Some(res.getId)
//        })
//      }
//    }
//  }
}
