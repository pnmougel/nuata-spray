package org.nuata.items

/**
 * Created by nico on 17/02/16.
 */
case class NameSearchQuery(name: String, searchInAliases: Boolean = true, lang: Option[String], method: String) {

}
