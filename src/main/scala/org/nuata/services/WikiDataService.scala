package org.nuata.services

import java.io.File

import org.json4s.Extraction._
import org.json4s.JsonAST.JObject
import org.json4s.JsonDSL._
import org.json4s.{DefaultFormats, Extraction}
import org.nuata.models.queries.SearchQuery
import org.nuata.repositories.{DimensionRepository, OoiRepository, _}
import org.nuata.services.routing.RouteRegistration
import org.nuata.shared._
import spray.routing.PathMatchers.Segment
import spray.routing._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future._
import scala.util.Try

/**
 * Created by nico on 27/12/15.
 */
trait WikiDataService extends RouteRegistration {
  val outPath = "/home/nico/data/wikidata/parts"

  def getWikiDataResource(id: String) : Option[File] = {
    if(id.size < 2) {
      None
    } else {
      val idParts = id.splitAt(1)
      val kind = idParts._1
      Try(idParts._2.toInt).toOption.map { num =>
        val firstLevel = num / 1000000
        val secondLevel = (num - (1000000 * firstLevel)) / 1000
        new File(outPath + "/" + kind + "/" + firstLevel + "/" + secondLevel + "/" + id + ".json")
      }.filter(_.exists())
    }
  }

  registerRoute {
    (path("wikidata" / Segment) & get) { id =>
      getWikiDataResource(id).map { file =>
        getFromFile(file)
      }.getOrElse {
        complete("invalid id")
      }
    }
  }
}
