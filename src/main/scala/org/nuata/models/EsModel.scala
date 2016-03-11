package org.nuata.models

/**
 * Created by nico on 24/02/16.
 */
trait EsModel[T] {
  def _id: Option[String]
  def _score: Option[Double]

  def withId(id: String) : T
}
