package org.nuata.models.edges

import org.nuata.models.datavalues._
/**
 * Created by nico on 13/02/16.
 */

trait Edge[T] {
  def attributeId: String
  def value: Option[T]
  def qualifiers: Option[Edges]
  def qualifiersOrder: Option[Array[String]]
  def sources: Option[Array[Edges]]
}