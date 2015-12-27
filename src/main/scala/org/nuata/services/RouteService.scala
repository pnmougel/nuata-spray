package org.nuata.services

import org.nuata.models.Dimension
import org.nuata.repositories.{OoiRepository, DimensionRepository, NameOperations, SearchOptions}
import org.nuata.shared.Json4sProtocol
import spray.http.MediaTypes._
import spray.http.StatusCodes
import spray.httpx.unmarshalling._
import spray.routing._
import spray.http._
import org.json4s.{Extraction, DefaultFormats}
import spray.httpx.{Json4sSupport, Json4sJacksonSupport}
import spray.routing._
import spray.http._
import MediaTypes._
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by nico on 23/12/15.
 */

trait RouteService  extends HttpService with Json4sProtocol {
  val readJson = path("test") {
    post {
      entity(as[List[Dimension]]) { person =>
        println(person)
        complete(StatusCodes.OK, "sfsdf")
      }
    } ~ post {
      entity(as[Dimension]) { person =>
        println(person)
        complete(StatusCodes.OK, "sfsdf")
      }
    }
  }

  val items = Map(
    "dimension" -> DimensionRepository,
    "ooi" -> OoiRepository
  )
  import scala.util.Try

  def getPositiveIntFromParam(key: String, params : Map[String, List[String]]): Option[Int] = {
    val param = params.getOrElse(key, List("")).head
    Try(param.toInt).toOption.filter(_ >= 0)
  }

  val routes = readJson :: (for((itemName, repository) <- items.toList) yield {
    path(itemName / "search") {
      get {
        parameterMultiMap { params =>
          val name = params.getOrElse("name", List("")).head
          val start = getPositiveIntFromParam("start", params).getOrElse(0)
          val limit = getPositiveIntFromParam("limit", params).getOrElse(10)
          val expand = getPositiveIntFromParam("expand", params).getOrElse(0)
          val filters = Map(
            "categoryIds" -> params.getOrElse("categoryIds", List()),
            "dimensionIds" -> params.getOrElse("dimensionIds", List()),
            "sourceIds" -> params.getOrElse("sourceIds", List())
          )

          val searchOptions = SearchOptions(name, NameOperations.StartsWith, start, limit, filters, expand = expand)
          println(searchOptions)
          complete( {
            repository.searchAndExpand(searchOptions).map( item => {
              Extraction.decompose(item)
            })
          })
        }
      }
    }
  })
  val myRoutes = routes.reduce((a, b) => { a ~ b })
}
