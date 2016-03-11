package org.nuata.models

import org.nuata.models.edges.Edges


/**
  * Created by nico on 13/02/16.
  */
case class Item(_id: Option[String], _score: Option[Double], labels: Map[String, Label],
                 instancesOf: Array[String],
                 subclassesOf: Array[String],
                 attributeIds: Array[String],
                 edges: Edges) extends EsModel[Item] {
                    def withId(id: String) = this.copy(_id = Some(id))
                  }
