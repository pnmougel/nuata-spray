package org.nuata.repositories

import java.util.Date

import com.sksamuel.elastic4s.ElasticDsl._
import org.json4s._
import org.nuata.shared.ElasticSearch

/**
 * Created by nico on 02/11/15.
 */
object QueryRepository  {
  def logQuery(query: String, dimensionIds: List[String], categoryIds: List[String], unitIds: List[String], ooiIds: List[String], ip: String) = {
    val log = Map("query" -> query,
      "dimensionIds" -> dimensionIds,
      "categoryIds" -> categoryIds,
      "unitIds" -> unitIds,
      "ooiIds" -> ooiIds,
      "ip" -> ip,
      "at" -> new Date())
    ElasticSearch.client.execute{
      index into "nuata" / "query" fields log
    }
  }
}