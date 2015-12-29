package org.nuata.shared


/**
 * Created by nico on 08/12/15.
 */

import java.util.Date

import NameOperations._

case class SearchOptions(name: String,
                         nameOperation: NameOperations = NameOperations.StartsWith,
                         start: Int = 0,
                         limit: Int = 10,
                         categoryId: List[String] = List(),
                         parentId: List[String] = List(),
                         sourceId: List[String] = List(),
                         fields: List[String] = List(),
                         from: List[String] = List(),
                          ints: List[Int],
                         expand: Int = 1) {
  val filters = Map(
    "categoryIds" -> categoryId,
    "parentIds" -> parentId,
    "sourceIds" -> sourceId
  )
}
