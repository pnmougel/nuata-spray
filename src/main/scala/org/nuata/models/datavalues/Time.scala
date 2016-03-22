package org.nuata.models.datavalues

/**
 * Created by nico on 18/02/16.
 */
case class Time(time: Option[String], year: Long, calendarId: String, precision: String, timeZone: BigInt) extends Value
