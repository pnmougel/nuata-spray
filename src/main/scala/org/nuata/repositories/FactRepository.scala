package org.nuata.repositories

import org.elasticsearch.action.search.SearchResponse
import org.json4s._
import org.nuata.models.Fact
import com.sksamuel.elastic4s.ElasticDsl._
import org.nuata.shared.ElasticSearch
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

/**
 * Created by nico on 02/11/15.
 */

object FactRepository extends BaseRepository[Fact]("fact") {
  def resultToEntity(res: SearchResponse) = res.as[Fact]

  protected def jsToInstance(jValue: JValue) = jValue.extract[Fact]

}
