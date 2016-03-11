package org.nuata.models.edges

import org.nuata.models.datavalues._
/**
 * Created by nico on 04/03/16.
 */
case class MathEdge(attributeId: String, value: Option[Math], qualifiers: Option[Edges], qualifiersOrder: Option[Array[String]], sources: Option[Array[Edges]])
  extends Edge[Math]
