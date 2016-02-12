package org.nuata.models.wikidata

import org.nuata.models.wikidata.datavalues._

/**
 * Created by nico on 09/02/16.
 */
case class EsDataValue(globeCoordinate: Option[GlobeCoordinate] = None,
                       monolingualText: Option[MonolingualText] = None,
                       quantity: Option[Quantity] = None,
                       string: Option[String] = None,
                       wikiItemId: Option[BigInt] = None,
                       wikiPropertyId: Option[BigInt] = None,
                       time: Option[TimeValue] = None)
