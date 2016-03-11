package org.nuata.models.datavalues

/**
 * Created by nico on 19/02/16.
 */
case class CommonsMedia(name: String,
                        url: Option[String] = None,
//                        extension: Option[String] = None,
//                        date: Option[String] = None,
                        licenseShortName: Option[String] = None,
//                        restrictions: Option[String] = None,
//                        usageTerms: Option[String] = None,
                        licenseUrl: Option[String] = None,
                        copyrighted: Option[Boolean] = None,
                        attributionRequired: Option[Boolean] = None,
                        license: Option[String] = None
//                        credit: Option[String] = None
//                        , description: Option[String] = None
                         )  extends Value
