package org.nuata.authentication.oauth

/**
 * Created by nico on 15/01/16.
 */

case class GoogleUserEmail(value: String)

case class GoogleUserImage(url: String, isDefault: Boolean)

case class GoogleUserInfo(id: String, displayName: Option[String], emails: Array[GoogleUserEmail], language: Option[String], image: Option[GoogleUserImage])
