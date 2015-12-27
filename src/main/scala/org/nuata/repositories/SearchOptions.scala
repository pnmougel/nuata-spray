package org.nuata.repositories

import org.nuata.repositories.NameOperations.NameOperations

/**
 * Created by nico on 08/12/15.
 */
case class SearchOptions(name: String,
                         nameOperation: NameOperations,
                         start: Int = 0,
                         limit: Int = 10,
                         filters: Map[String, List[String]] = Map(),
                         fields: List[String] = List(),
                         expand: Int = 1) {
}
