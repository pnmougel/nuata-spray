package org.nuata.authentication.oauth.google

case class GoogleUserInfo(id: String, displayName: Option[String], emails: Array[GoogleUserEmail], language: Option[String], image: Option[GoogleUserImage])
