package org.nuata.authentication.oauth.github

import org.json4s.jackson.Serialization._
import org.nuata.authentication.Role
import org.nuata.authentication.oauth.facebook.FacebookUserInfo
import org.nuata.authentication.oauth.{OAuthCode, OAuthProvider}
import org.nuata.core.settings.Settings
import org.nuata.users.User
import spray.client.pipelining._
import spray.http.HttpResponse

/**
 * Created by nico on 23/03/16.
 */
object GithubAuth extends OAuthProvider {
  val clientId = Settings.getString("github.clientId")
  val clientSecret = Settings.getString("github.clientSecret")

  val path = "github"

  def tokenQuery(oauthCode: OAuthCode) = Get(s"https://github.com/login/oauth/access_token?code=${oauthCode.code}&client_id=${clientId}&client_secret=${clientSecret}")

  def userInfoQuery = Get("https://api.github.com/user")

  def getUser(response: HttpResponse): User = {
    val userInfo = read[GithubUserInfo](response.entity.asString)
    User(None, None, userInfo.email, userInfo.name, Role.User)
  }
}
