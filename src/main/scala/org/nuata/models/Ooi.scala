package org.nuata.models

import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.{DefaultFormats, Extraction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.nuata.repositories._

/**
 * Created by nico on 02/11/15.
 */
case class Ooi(_id: Option[String],
                    _score: Option[Double],
                    name: Map[String, String],
                    otherNames: Map[String, List[String]],
                    description: Map[String, String],
                    unitIds: List[String],
                    sourceIds: List[String],
                    meta: Map[String, _])
  extends LocalizedNamedModel(_id, _score, name, otherNames, description, meta)
  with JsonSerializable {

  implicit val formats = DefaultFormats

  lazy val sources = Future.sequence(for(sourceId <- sourceIds) yield { SourceRepository.byId(sourceId) })
  val units = Future.sequence(for(unitId <- unitIds) yield { UnitRepository.byId(unitId) })

  override def getIndexQuery() = {
    defaultIndexQuery ++ Map("unitIds" -> unitIds) ++ Map("sourceIds" -> sourceIds)
  }
  override def getSearchQuery() = defaultSearchQuery
  override def getMatchQuery() = defaultMatchQuery

  def toJson(level: Int = -1) : Future[JObject] = {
    if(level == 0) {
      Future(("_id" -> _id) ~
        ("_score" -> _score) ~
        ("name" -> Extraction.decompose(name)) ~
        ("otherNames" -> Extraction.decompose(otherNames)) ~
        ("descriptions" -> Extraction.decompose(description)) ~
        ("units" -> unitIds) ~
        ("meta" -> Extraction.decompose(meta)) ~
        ("sources" -> sourceIds))
    } else {
      for(unitsJson <- toJsonSeq(units, level - 1);
          sourcesList <- toJsonSeq(sources, level - 1)) yield {
        ("_id" -> _id) ~
          ("_score" -> _score) ~
          ("name" -> Extraction.decompose(name)) ~
          ("otherNames" -> Extraction.decompose(otherNames)) ~
          ("descriptions" -> Extraction.decompose(description)) ~
          ("units" -> unitsJson) ~
          ("meta" -> Extraction.decompose(meta)) ~
          ("sources" -> sourcesList)
      }
    }

  }
}
