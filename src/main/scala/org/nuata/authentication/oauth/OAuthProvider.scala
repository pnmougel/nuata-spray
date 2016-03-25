package org.nuata.authentication.oauth

import org.json4s.DefaultFormats
import org.nuata.users.User
import spray.http.{HttpRequest, HttpResponse}

/**
 * Created by nico on 23/03/16.
 */
trait OAuthProvider {
  implicit val formats = DefaultFormats

  def path: String

  def tokenQuery(oauthCode: OAuthCode) : HttpRequest

  def userInfoQuery: HttpRequest

  def getUser(response: HttpResponse): User
}
