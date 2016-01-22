package org.nuata.repositories

import org.elasticsearch.action.search.SearchResponse
import org.json4s._
import com.sksamuel.elastic4s.ElasticDsl
import org.nuata.models.Category
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.nuata.shared.ElasticSearch

/**
 * Created by nico on 02/11/15.
 */

object CategoryRepository extends BaseRepository[Category]("category") {
}