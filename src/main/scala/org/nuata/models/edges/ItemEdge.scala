package org.nuata.models.edges

import org.nuata.models.datavalues._
/**
 * Created by nico on 04/03/16.
 */
case class ItemEdge(attributeId: String, value: Option[ItemRef], qualifiers: Option[Edges], qualifiersOrder: Option[Array[String]], sources: Option[Array[Edges]])
  extends Edge[ItemRef]
