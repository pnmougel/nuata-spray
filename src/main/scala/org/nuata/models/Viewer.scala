package org.nuata.models

/**
 * Created by nico on 15/03/16.
 */
case class Viewer(_id: Option[String], _score: Option[Double], name: String, description: String) extends EsModel[Viewer] {
  def withId(id: String) = this.copy(_id = Some(id))
}