package org.nuata.models

import org.nuata.models.edges.Edges

/**
  * Created by nico on 17/02/16.
  */

case class Attribute(_id: Option[String], _score: Option[Double],
                      labels: Map[String, Label],
                      valueType: String,
                      instancesOf: Array[String],
                      subclassesOf: Array[String],
                      attributeIds: Array[String],
                      edges: Edges) extends EsModel[Attribute] {
                         def withId(id: String) = this.copy(_id = Some(id))
                       }
