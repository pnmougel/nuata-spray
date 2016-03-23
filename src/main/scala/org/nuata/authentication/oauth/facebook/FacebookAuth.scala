package org.nuata.authentication.oauth.facebook

import org.json4s.jackson.Serialization._
import org.nuata.authentication.Role
import org.nuata.authentication.oauth.{OAuthCode, OAuthProvider}
import org.nuata.core.settings.Settings
import org.nuata.users.User
import spray.client.pipelining._
import spray.http.HttpResponse

/**
 * Created by nico on 23/03/16.
 */
object FacebookAuth extends OAuthProvider {
  val clientId = Settings.getString("facebook.clientId")
  val clientSecret = Settings.getString("facebook.clientSecret")
  val apiVersion = Settings.getString("facebook.apiVersion")
  val redirectUri = Settings.getString("facebook.redirectUri")

  val path = "facebook"

  def tokenQuery(oauthCode: OAuthCode) = Get(s"https://graph.facebook.com/${apiVersion}/oauth/access_token?client_id=${clientId}&redirect_uri=${redirectUri}&client_secret=${clientSecret}&code=${oauthCode.code}")

  def userInfoQuery = Get(s"https://graph.facebook.com/${apiVersion}/me?fields=id,name,email,verified")

  def getUser(response: HttpResponse): User = {
    val userInfo = read[FacebookUserInfo](response.entity.asString)
    User(None, None, userInfo.email, userInfo.name, Role.User)
  }
}
