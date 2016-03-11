package org.nuata.models.datavalues

/**
 * Created by nico on 18/02/16.
 */
case class Time(time: String, before: Option[BigInt], after: Option[BigInt], calendarId: String, precision: BigInt, timeZone: BigInt) extends Value
