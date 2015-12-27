package org.nuata.repositories

import org.elasticsearch.action.search.SearchResponse
import org.json4s._
import org.nuata.models.Dimension
import org.nuata.repositories.commons.LocalizedNamedItemRepository
import com.sksamuel.elastic4s.ElasticDsl._
import org.nuata.shared.ElasticSearch
import scala.concurrent.ExecutionContext.Implicits.global
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

/**
 * Created by nico on 02/11/15.
 */
object DimensionRepository extends LocalizedNamedItemRepository[Dimension]("dimension") {
  implicit val formats = DefaultFormats
  protected def jsToInstance(jValue: JValue) = jValue.extract[Dimension]

  def resultToEntity(res: SearchResponse) = res.as[Dimension]

  def removeDependency(dimensionId: String, dependencyId: String, dependencyType: String) = {
    byIdOpt(dimensionId).map(dimensionOpt => {
      for(dimension <- dimensionOpt) yield {
        val prevList = dependencyType match {
          case "parentIds" => dimension.parentIds
          case "categoryIds" => dimension.categoryIds
          case _ => List[String]()
        }
        val newIds = prevList.filter(_ != dependencyId)
        client.execute {
          update id dimensionId in path docAsUpsert (dependencyType -> newIds)
        }
      }
    })
  }
}