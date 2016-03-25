package org.nuata.authentication.oauth

/**
 * Created by nico on 15/01/16.
 */
case class TokenResponse(accessToken: String, tokenType: Option[String], expiresIn: Option[String], scope: Option[String])