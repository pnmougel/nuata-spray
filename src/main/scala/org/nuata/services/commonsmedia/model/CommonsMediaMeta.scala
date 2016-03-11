package org.nuata.services.commonsmedia.model

/**
 * Created by nico on 19/02/16.
 */
case class CommonsMediaMeta(DateTime: Option[CommonsMediaMetaInfo],
                         LicenseShortName: Option[CommonsMediaMetaInfo],
                         Restrictions: Option[CommonsMediaMetaInfo],
                         UsageTerms: Option[CommonsMediaMetaInfo],
                         LicenseUrl: Option[CommonsMediaMetaInfo],
                         Copyrighted: Option[CommonsMediaMetaInfo],
                         AttributionRequired: Option[CommonsMediaMetaInfo],
                         License: Option[CommonsMediaMetaInfo],
                         Credit: Option[CommonsMediaMetaInfo],
                         ImageDescription: Option[CommonsMediaMetaInfo]) {

  def getStringFromMeta(info : Option[CommonsMediaMetaInfo]): Option[String] = {
    info.map(_.value.trim).flatMap { v =>
      if(v.isEmpty) None else Some(v)
    }
  }

  def getBooleanFromMeta(info : Option[CommonsMediaMetaInfo]): Option[Boolean] = {
    info.map(_.value.toLowerCase).flatMap { v =>
      if(v == "false" || v == "0") {
        Some(false)
      } else if(v == "true" || v == "1") {
        Some(true)
      } else {
        None
      }
    }
  }
}
