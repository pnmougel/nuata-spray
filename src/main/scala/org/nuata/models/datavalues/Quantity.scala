package org.nuata.models.datavalues

/**
 * Created by nico on 18/02/16.
 */
case class Quantity(amount: Double, amountExact: String,
                     upperBound: Option[Double], upperBoundExact: Option[String],
                     lowerBound: Option[Double], lowerBoundExact: Option[String],
                     unit: Option[String]) extends Value