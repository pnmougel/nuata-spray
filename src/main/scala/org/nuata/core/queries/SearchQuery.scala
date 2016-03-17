package org.nuata.core.queries

import com.sksamuel.elastic4s.{SortDefinition, QueryDefinition}
import com.sksamuel.elastic4s.ElasticDsl._

/**
 * Created by nico on 15/03/16.
 */
trait SearchQuery {
  def page: Int
  def limit: Int
  def query: QueryDefinition

  def sort : Seq[SortDefinition] = List()

  val start = Math.max(0, (page - 1) * limit)

  def mergeQuery(queries: QueryDefinition*) = {
    bool(must(queries : _*))
  }
}

case class BaseSearchQuery(page: Int = 1, limit: Int = 5) extends SearchQuery {
  def query = matchAllQuery
}
