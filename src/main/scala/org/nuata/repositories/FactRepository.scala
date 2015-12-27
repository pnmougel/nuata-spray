package org.nuata.repositories

import org.elasticsearch.action.search.SearchResponse
import org.json4s._
import org.nuata.models.Fact
import org.nuata.repositories.commons.LocalizedNamedItemRepository
import com.sksamuel.elastic4s.ElasticDsl._
import org.nuata.shared.ElasticSearch
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

/**
 * Created by nico on 02/11/15.
 */

object FactRepository extends LocalizedNamedItemRepository[Fact]("fact") {
  implicit val formats = DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all

  def resultToEntity(res: SearchResponse) = res.as[Fact]

  protected def jsToInstance(jValue: JValue) = jValue.extract[Fact]

  def indexFacts(facts: List[Fact]): Future[Array[String]] = {
    val indexQueries = facts.map( fact => {
      var indexQuery = Map[String, Any]("dimensionIds" -> fact.dimensionIds, "ooiIds" -> fact.ooiIds, "sourceIds" -> fact.sourceIds)
      for(v <- fact.value) { indexQuery += ("value" -> v) }
      for(v <- fact.valueInt) { indexQuery += ("valueInt" -> v) }
      for(v <- fact.at) { indexQuery += ("at" -> v) }
      index into path fields indexQuery
    })
    client.execute(bulk (indexQueries) ).map( results => {
      results.getItems.map( _.getId )
    })
  }
}
