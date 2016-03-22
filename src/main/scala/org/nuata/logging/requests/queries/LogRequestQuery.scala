package org.nuata.logging.requests.queries

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.search.sort.SortOrder
import org.nuata.core.queries.SearchQuery

/**
 * Created by nico on 22/03/16.
 */
case class LogRequestQuery(path: Option[String], ip: Option[String], method: Option[String], status: Option[Int] = None, page: Int = 1, limit: Int = 5) extends SearchQuery {
  def query = {
    val pathQuery = matchAllOr(method, value => termQuery("path", value))
    val methodQuery = matchAllOr(method, value => termQuery("method", value))
    val statusQuery = status.map { value =>
      termQuery("status", value)
    }.getOrElse(matchAllQuery)

    mergeQuery(pathQuery, methodQuery, statusQuery)
  }

  override def sort = {
    List(field sort "created_at" order( SortOrder.DESC))
  }
}
