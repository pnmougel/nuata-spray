package org.nuata.tasks.queries

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.search.sort.SortOrder
import org.nuata.core.queries.SearchQuery

/**
 * Created by nico on 17/03/16.
 */
case class TaskQuery(name: Option[String], status: Option[String] = None, page: Int = 1, limit: Int = 5) extends SearchQuery {
  def query = {
    val nameQuery = name.map { name =>
      if(name.isEmpty) {
        matchAllQuery
      } else {
        termQuery("name", name)
      }
    }.getOrElse(matchAllQuery)

    val statusQuery = status.map { status =>
      if(status.toLowerCase == "all" || status.isEmpty) {
        matchAllQuery
      } else {
        termQuery("status", status)
      }
    }.getOrElse(matchAllQuery)

    mergeQuery(nameQuery, statusQuery)
  }

  override def sort = {
    List(field sort "createdAt" order( SortOrder.DESC))
  }
}
