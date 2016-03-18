package org.nuata.attributes.queries

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.search.sort.SortOrder
import org.nuata.core.queries.SearchQuery

/**
 * Created by nico on 24/02/16.
 */
case class AttributeSearchQuery(name: String, page: Int, limit: Int, lang: String = "en", valueType: String = "", instanceOf: Option[String] = None) extends SearchQuery {
  def query = {
    val nameQuery = if(name.trim.isEmpty) {
      matchAllQuery
    } else {
      //      nestedQuery(s"labels.${query.lang}").query(matchQuery(s"labels.${query.lang}.name", query.name))
      nestedQuery(s"labels.${lang}").query(prefixQuery(s"labels.${lang}.name", name))
    }

    val instanceOfQuery = instanceOf.map { id =>
      if(id.isEmpty) {
        matchAllQuery
      } else {
        termQuery("instancesOf", id)
      }
    }.getOrElse(matchAllQuery)

    val dataTypeQuery = if(valueType.trim.isEmpty) {
      matchAllQuery
    } else {
      termQuery("valueType", valueType)
    }

    mergeQuery(nameQuery, instanceOfQuery, dataTypeQuery)
  }

  override def sort = {
    List(field sort "nbItems" order( SortOrder.DESC))
  }
}
