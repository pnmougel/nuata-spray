package org.nuata.models.queries

/**
 * Created by nico on 08/12/15.
 */

import org.nuata.shared.NameOperations
import org.nuata.shared.NameOperations._

case class SearchQuery(name: String = "",
                         nameOperation: NameOperations = NameOperations.StartsWith,
                         start: Int = 0,
                         limit: Int = 10,
                         categoryId: List[String] = List(),
                         parentId: List[String] = List(),
                         hasParentId: List[String] = List(),
                         sourceId: List[String] = List(),
                         fields: List[String] = List(),
                         from: List[String] = List(),
                         expand: Int = 1) {
  val filters = Map(
    "categoryIds" -> categoryId,
    "parentIds" -> parentId,
    "sourceIds" -> sourceId
  )
}
