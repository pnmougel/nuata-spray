package org.nuata.authentication.oauth.google

import org.json4s.jackson.Serialization._
import org.nuata.authentication.Role
import org.nuata.authentication.oauth.github.GithubUserInfo
import org.nuata.authentication.oauth.{OAuthCode, OAuthProvider}
import org.nuata.core.settings.Settings
import org.nuata.users.User
import spray.client.pipelining._
import spray.http.HttpResponse

/**
 * Created by nico on 23/03/16.
 */
object GoogleAuth extends OAuthProvider {
  val clientId = Settings.getString("google.clientId")
  val clientSecret = Settings.getString("google.clientSecret")
  val apiVersion = Settings.getString("google.apiVersion")

  val path = "google"

  def tokenQuery(oauthCode: OAuthCode) = Post(s"https://www.googleapis.com/oauth2/${apiVersion}/token?client_id=${clientId}&redirect_uri=${oauthCode.redirectUri}&client_secret=${clientSecret}&code=${oauthCode.code}&grant_type=authorization_code")

  def userInfoQuery = Get("https://www.googleapis.com/plus/v1/people/me")

  def getUser(response: HttpResponse): User = {
    val userInfo = read[GoogleUserInfo](response.entity.asString)
    User(None, None, userInfo.emails.head.value, userInfo.displayName, Role.User)
  }
}
