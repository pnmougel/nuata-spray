package org.nuata.models

/**
 * Created by nico on 03/11/15.
 */
abstract class EsModel[T](_id: Option[String], _score: Option[Double]) {
  def withId(_id: String) : T
}
