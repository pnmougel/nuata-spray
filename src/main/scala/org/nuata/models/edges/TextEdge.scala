package org.nuata.models.edges

import org.nuata.models.datavalues._
/**
 * Created by nico on 04/03/16.
 */
case class TextEdge(attributeId: String, value: Option[Text], qualifiers: Option[Edges], qualifiersOrder: Option[Array[String]], sources: Option[Array[Edges]])
  extends Edge[Text]
