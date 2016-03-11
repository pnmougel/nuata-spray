package org.nuata.services.commonsmedia.model

import org.nuata.models.EsModel

/**
 * Created by nico on 25/02/16.
 */


case class MediaInfo(_id: Option[String] = None,
                     _score: Option[Double] = None,
                     name: String,
                     url: Option[String] = None,
                     extension: String,
                     date: Option[String] = None,
                     licenseShortName: Option[String] = None,
                     restrictions: Option[String] = None,
                     usageTerms: Option[String] = None,
                     licenseUrl: Option[String] = None,
                     copyrighted: Option[Boolean] = None,
                     attributionRequired: Option[Boolean] = None,
                     license: Option[String] = None,
                     credit: Option[String] = None,
                     description: Option[String] = None)
  extends EsModel[MediaInfo] {

  def withId(id: String) = this.copy(_id = Some(id))
}