package org.nuata.services

import org.json4s.JsonAST.JObject
import org.json4s.JsonDSL._
import org.json4s.{DefaultFormats, Extraction}
import org.nuata.repositories.{DimensionRepository, OoiRepository, _}
import org.nuata.shared._
import spray.routing._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by nico on 27/12/15.
 */
trait SearchService extends HttpService with Json4sProtocol {
  implicit val formats = DefaultFormats

  val items = Map(
    "dimension" -> DimensionRepository,
    "ooi" -> OoiRepository,
    "category" -> CategoryRepository,
    "fact" -> FactRepository,
    "source" -> SourceRepository,
    "unit" -> UnitRepository
  )


  val searchRoutes = path("search") {
    get {
      parameterMultiMap { params =>
        QueryParams.as[SearchOptions](params) match {
          case Left(errors) => reject(MalformedQueryParamRejection(errors._1, errors._2))
          case Right(searchOptions) => {
            println(searchOptions.ints)

            val repositories = searchOptions.from.map(items)
            complete({
              Future.sequence(repositories.map(repository => {
                repository.searchAndExpand(searchOptions).map(jsonValues => {
                  (repository.`type`, Extraction.decompose(jsonValues))
                })
              })).map(seqOfJson => {
                Extraction.decompose(seqOfJson.foldLeft(JObject()) { (value, acc) => value ~ acc })
              })
            })
          }
        }
      }
    }
  }
}
