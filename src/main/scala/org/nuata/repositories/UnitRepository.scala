package org.nuata.repositories

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.elasticsearch.action.search.SearchResponse
import org.json4s._
import org.nuata.models.Unit

/**
 * Created by nico on 02/11/15.
 */
object UnitRepository extends BaseRepository[Unit]("unit") {
  implicit val formats = DefaultFormats
  protected def jsToInstance(jValue: JValue) = jValue.extract[Unit]

  def resultToEntity(res: SearchResponse) = res.as[Unit]
}
