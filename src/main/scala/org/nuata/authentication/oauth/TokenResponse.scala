package org.nuata.authentication.oauth

/**
 * Created by nico on 15/01/16.
 */
case class TokenResponse(access_token: String, token_type: Option[String], expires_in: Option[String], scope: Option[String]) {

}
