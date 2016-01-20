package org.nuata.services

import org.json4s.Extraction
import org.nuata.models._
import org.nuata.repositories._
import org.nuata.services.routing.RouteRegistration
import org.nuata.shared.{ElasticSearch, Json4sProtocol}
import spray.http.StatusCodes._
import spray.routing._
import scala.concurrent.ExecutionContext.Implicits.global
import com.sksamuel.elastic4s.ElasticDsl._
import scala.concurrent.Future._
/**
 * Created by nico on 31/12/15.
 */
trait SearchFactService extends RouteRegistration with Json4sProtocol {
  val validItemTypes = Map("category" -> "categoryIds", "dimension" -> "dimensionIds", "unit" -> "", "ooi" -> "ooiIds")

  /*
  def searchFacts(query: String, dimensionIds: List[String], categoryIds: List[String], unitIds: List[String], ooiIds: List[String]) {
    // Log the query
//    QueryRepository.logQuery(query, dimensionIds, categoryIds, unitIds, ooiIds, rs.remoteAddress)

    val defaultTermQueries = List(
      filteredQuery filter termsFilter("dimensionIds", dimensionIds : _*),
      filteredQuery filter termsFilter("ooiIds", ooiIds : _*)
    )

    val esQuery = search in "nuata" / "fact" query bool { must(defaultTermQueries)}

    ElasticSearch.client.execute { esQuery }.flatMap( facts => {
      val xFactModels = facts.as[Fact]
      val factModels = (for(fact <- xFactModels) yield {
        val isAllDimensionsInQuery = !fact.dimensionIds.exists( dimensionId => !dimensionIds.contains(dimensionId) )
        (fact, isAllDimensionsInQuery)
      }).filter(_._2).reverse.map(_._1)
      sequence(for(factModel <- factModels.toList) yield { factModel.toJson() }).map( facts => {
        Extraction.decompose(facts)
      })
    })
  }
  */
}
