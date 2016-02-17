package org.nuata.models

/**
 * Created by nico on 13/02/16.
 */
case class Item(_id: Option[String], _score: Option[Double], labels: Map[String, Label], edges: Map[String, Array[Edge]]) {

}
