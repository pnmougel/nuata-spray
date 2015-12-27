package org.nuata.repositories

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.ElasticDsl
import org.nuata.shared.ElasticSearch

/**
 * Created by nico on 02/11/15.
 */
class BaseRepository(`type`: String) {
  val path = "nuata" / `type`
  val client = ElasticSearch.client

  def count = {
    ElasticSearch.client.execute { ElasticDsl.count from "nuata" types `type` }
  }

}
