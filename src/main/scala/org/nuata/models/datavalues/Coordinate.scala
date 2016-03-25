package org.nuata.models.datavalues

/**
 * Created by nico on 18/02/16.
 */
case class Coordinate(latitude: Option[Double], longitude: Option[Double], precision: Option[Double], globeId: String) extends Value
