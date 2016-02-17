package org.nuata.models

import org.nuata.models.datavalues.DataValue

/**
 * Created by nico on 13/02/16.
 */
case class Edge(attributeId: String, value: DataValue, qualifiers: Array[Edge], sources: Array[Array[Edge]]) {

}
