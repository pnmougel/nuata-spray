package org.nuata.repositories

import org.elasticsearch.action.search.SearchResponse
import org.json4s._
import org.nuata.models.Ooi
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.nuata.shared.ElasticSearch

/**
 * Created by nico on 02/11/15.
 */
object OoiRepository extends BaseRepository[Ooi]("ooi") {
  implicit val formats = DefaultFormats
  protected def jsToInstance(jValue: JValue) = jValue.extract[Ooi]

  def resultToEntity(res: SearchResponse) = res.as[Ooi]

}
