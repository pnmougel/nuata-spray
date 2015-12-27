package org.nuata.repositories

import org.elasticsearch.action.search.SearchResponse
import org.json4s._
import org.nuata.models.Source
import org.nuata.repositories.commons.LocalizedNamedItemRepository
import org.nuata.shared.ElasticSearch

import scala.concurrent.Future
import com.sksamuel.elastic4s.ElasticDsl._
import scala.concurrent.ExecutionContext.Implicits.global
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

/**
 * Created by nico on 02/11/15.
 */

object SourceRepository extends LocalizedNamedItemRepository[Source]("source") {
  implicit val formats = DefaultFormats

  def resultToEntity(res: SearchResponse) = res.as[Source]

  protected def jsToInstance(jValue: JValue) = jValue.extract[Source]

  def indexSources(sources: List[Source]): Future[Array[String]] = {
    val indexQueries = sources.map( source => {
      var indexQuery = Map[String, Any](
        "name" -> source.name, "kind" -> source.kind, "authors" -> source.authors)
      for(v <- source.url) { indexQuery += ("url" -> v) }
      index into path fields indexQuery
    })
    client.execute(bulk (indexQueries) ).map( results => {
      results.getItems.map( _.getId )
    })
  }
}
