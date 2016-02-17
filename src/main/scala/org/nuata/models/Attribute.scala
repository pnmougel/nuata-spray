package org.nuata.models

/**
 * Created by nico on 17/02/16.
 */
case class Attribute(_id: Option[String], _score: Option[Double], labels: Map[String, Label], edges: Map[String, Array[Edge]], dataTypeId: String)
