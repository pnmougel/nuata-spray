package org.nuata.services

import com.typesafe.config.ConfigFactory
import org.json4s.JsonAST.JObject
import org.json4s.JsonDSL._
import org.json4s.{DefaultFormats, Extraction}
import org.nuata.models.queries.SearchQuery
import org.nuata.repositories.{DimensionRepository, OoiRepository, _}
import org.nuata.services.routing.RouteRegistration
import org.nuata.shared._
import spray.routing._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future._
import org.json4s.Extraction._

/**
 * Created by nico on 27/12/15.
 */
trait SearchService extends RouteRegistration with Json4sProtocol {
  implicit val formats = DefaultFormats

  val items = Map(
    "dimension" -> DimensionRepository,
    "ooi" -> OoiRepository,
    "category" -> CategoryRepository,
    "fact" -> FactRepository,
    "source" -> SourceRepository,
    "unit" -> UnitRepository
  )

  registerRoute {
    (path("search") & get & parameterMultiMap) { params =>
      QueryParams.as[SearchQuery](params) match {
        case Left(errors) => reject(MalformedQueryParamRejection(errors._1, errors._2))
        case Right(searchOptions) => {
          val repositories = searchOptions.from.map(items)
          complete({
            sequence(repositories.map(repository => {
              repository.searchAndExpand(searchOptions).map(jsonValues => {
                (repository.`type`, Extraction.decompose(jsonValues))
              })
            })).map(seqOfJson => {
              decompose(seqOfJson.foldLeft(JObject()) { (value, acc) => value ~ acc })
            })
          })
        }
      }
    } ~ (path("children") & get & complete) {
       DimensionRepository.getChildren("AVIX-ja0v3JqZyWk6lhj").map( res => {
        res + " result"
      })
  //    DimensionRepository.children2("AVH1tyWC_1h-G2V3Dv_1", 6).map( res => {
  //      res.size + " results"
  //    })
  //    DimensionRepository.byId("AVH1tyWC_1h-G2V3Dv_1").map( child => {
  //      DimensionRepository.children(child, 7).map( res => {
  //        res.size + " results"
  //      })
  //    })
  //      DimensionRepository.children4("AVH1tyWC_1h-G2V3Dv_1", 7).map( res => {
  //        res.size + " results"
  //      })

    } ~ (path("up") & get & complete) {
      DimensionRepository.list()
      "ok"
    }
  }
}
