package org.nuata.models.edges

import org.nuata.models.datavalues.AttributeRef


/**
 * Created by nico on 04/03/16.
 */
case class AttributeEdge(attributeId: String, value: Option[AttributeRef],
                         qualifiers: Option[Edges],
                         qualifiersOrder: Option[Array[String]],
                         sources: Option[Array[Edges]])
  extends Edge[AttributeRef]
