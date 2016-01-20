package org.nuata.authentication.oauth.facebook

/**
 * Created by nico on 13/01/16.
 */
case class FacebookToken(access_token: String, token_type: Option[String], expires_in: Option[String])
