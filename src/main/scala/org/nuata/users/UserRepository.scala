package org.nuata.users

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.{ObjectType, StringType}
import com.sksamuel.elastic4s.mappings._
import org.nuata.authentication.Role
import org.nuata.authentication.bearer.BearerToken
import org.nuata.core.BaseRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random
import org.nuata.core.json.ESJackson._

/**
 * Created by nico on 29/12/15.
 */
object UserRepository extends BaseRepository[User]("account", Some("user")) {
  override def indexMapping = {
    mapping("account").fields(
      field("email") typed StringType index "not_analyzed",
      field("name") typed StringType index "not_analyzed",
      field("roles") typed ObjectType,
      field("token") typed StringType index "not_analyzed",
      field("password") typed StringType index "not_analyzed"
    )
  }

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
      search in path query termQuery("token", token.token)
    }.map(res => {
      if(res.totalHits == 1) {
        Some(res.as[User].head)
      } else {
        None
      }
    })
  }

  def getOrCreateUserTokenFromOAuth(user: User) : Future[String] = {
    client.execute { search in path query termQuery("email", user.email) }.flatMap { res =>
      if(res.totalHits == 0) {
        UserRepository.index(user).flatMap(res => {
          generateTokenForUser(res.getId)
        })
      } else {
        generateTokenForUser(res.hits.head.id)
      }
    }
  }

  def createDefaultAdminAccount(email: String, password: String) : Future[String] = {
    println(password)
    val user = User(None, None, email, Some("admin"), Role.Admin, Some(password))
    UserRepository.index(user).flatMap(res => {
      generateTokenForUser(res.getId)
    })
  }

  def getUserByEmailAndPassword(email: String, password: String) : Future[Option[User]] = {
    client.execute { search in path query
      bool {
        must {
          Seq(termQuery("email", email),
          termQuery("password", password))
        }
      }
    }.map { res =>
      res.as[User].headOption
    }
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
