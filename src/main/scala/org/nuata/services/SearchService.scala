package org.nuata.services

import org.json4s.Extraction
import org.json4s.JsonAST.JObject
import org.nuata.repositories._
import spray.routing._
import org.nuata.models.Dimension
import org.nuata.repositories.{OoiRepository, DimensionRepository, NameOperations, SearchOptions}
import org.nuata.shared.Json4sProtocol
import spray.http.StatusCodes
import spray.httpx.unmarshalling._
import org.json4s.{Extraction, DefaultFormats}
import spray.httpx.{Json4sSupport, Json4sJacksonSupport}
import spray.routing._
import spray.http._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.json4s.JsonDSL._

/**
 * Created by nico on 27/12/15.
 */
trait SearchService extends HttpService with Json4sProtocol {
  val items = Map(
    "dimension" -> DimensionRepository,
    "ooi" -> OoiRepository,
    "category" -> CategoryRepository,
    "fact" -> FactRepository,
    "source" -> SourceRepository,
    "unit" -> UnitRepository
  )
  import scala.util.Try

  def getPositiveIntFromParam(key: String, params : Map[String, List[String]]): Option[Int] = {
    val param = params.getOrElse(key, List("")).head
    Try(param.toInt).toOption.filter(_ >= 0)
  }


  val searchRoutes = path("search") {
    get {
      parameterMultiMap { params =>
        val name = params.getOrElse("name", List("")).head
        val start = getPositiveIntFromParam("start", params).getOrElse(0)
        val limit = getPositiveIntFromParam("limit", params).getOrElse(10)
        val expand = getPositiveIntFromParam("expand", params).getOrElse(0)
        val repositories = params.getOrElse("from", List("")).map(items)
        val filters = Map(
          "categoryIds" -> params.getOrElse("categoryIds", List()),
          "dimensionIds" -> params.getOrElse("dimensionIds", List()),
          "sourceIds" -> params.getOrElse("sourceIds", List())
        )

        val searchOptions = SearchOptions(name, NameOperations.StartsWith, start, limit, filters, expand = expand)
        complete( {
          Future.sequence(repositories.map(repository => {
            repository.searchAndExpand(searchOptions).map( jsonValues => {
              (repository.`type`, Extraction.decompose(jsonValues))
            } )
          })).map( seqOfJson => {
            Extraction.decompose(seqOfJson.foldLeft(JObject()) { (value, acc) => value ~ acc })
          })
        })
      }
    }
  }

  /*
  val searchRoutes = (for((itemName, repository) <- items.toList) yield {
    path(itemName / "search") {
      get {
        parameterMultiMap { params =>
          val name = params.getOrElse("name", List("")).head
          val start = getPositiveIntFromParam("start", params).getOrElse(0)
          val limit = getPositiveIntFromParam("limit", params).getOrElse(10)
          val expand = getPositiveIntFromParam("expand", params).getOrElse(0)
          val repositories = params.getOrElse("from", List("")).map(items)
          val filters = Map(
            "categoryIds" -> params.getOrElse("categoryIds", List()),
            "dimensionIds" -> params.getOrElse("dimensionIds", List()),
            "sourceIds" -> params.getOrElse("sourceIds", List())
          )

          val searchOptions = SearchOptions(name, NameOperations.StartsWith, start, limit, filters, expand = expand)
          println(searchOptions)
          complete( {
            Future.sequence(repositories.map(repository => {
              repository.searchAndExpand(searchOptions).map( jsonValues => {
                (repository.`type`, Extraction.decompose(jsonValues))
              } )
            })).map( seqOfJson => {
              Extraction.decompose(seqOfJson.foldLeft(JObject()) { (value, acc) => value ~ acc })
            })
            //            repository.searchAndExpand(searchOptions).map( item => {
            //              Extraction.decompose(item)
            //            })
          })
        }
      }
    }
  }).reduce((a, b) => { a ~ b })
  */
}
