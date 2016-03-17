package org.nuata.attributes.queries

/**
 * Created by nico on 24/02/16.
 */
case class AttributeQuery(name: String, page: Int, limit: Int, lang: String = "en", valueType: String = "")
