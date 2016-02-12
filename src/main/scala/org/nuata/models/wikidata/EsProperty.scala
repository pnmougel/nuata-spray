package org.nuata.models.wikidata

/**
 * Created by nico on 08/02/16.
 */
case class EsProperty(id: String, claims: Array[EsClaim], dataType: Option[String], labels: Array[EsLocal])
