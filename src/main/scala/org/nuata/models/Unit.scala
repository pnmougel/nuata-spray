package org.nuata.models

import org.json4s.JsonAST.JObject
import org.json4s.JsonDSL._
import org.json4s.{Extraction, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.nuata.repositories._

/**
 * Created by nico on 02/11/15.
 */
case class Unit(_id: Option[String],
                     _score: Option[Double],
                     kind: String,
                     name: Map[String, String],
                     otherNames: Map[String, List[String]],
                     description: Map[String, String],
                     sourceIds: List[String],
                     meta: Map[String, _])
  extends BaseModel(_id, _score)
  with JsonSerializable {

  def toJson(level: Int = -1) : Future[JObject] = {
    implicit val formats = DefaultFormats
    if(level == 0) {
      Future(("_id" -> _id) ~
        ("_score" -> _score) ~
        ("kind" -> Extraction.decompose(kind)) ~
        ("name" -> Extraction.decompose(name)) ~
        ("otherNames" -> Extraction.decompose(otherNames)) ~
        ("description" -> Extraction.decompose(description)) ~
        ("meta" -> Extraction.decompose(meta)) ~
        ("sources" -> sourceIds))
    } else {
      val sources = Future.sequence(for(sourceId <- sourceIds) yield { SourceRepository.byId(sourceId) })

      for(sourcesList <- toJsonSeq(sources, level - 1)) yield {
        ("_id" -> _id) ~
          ("_score" -> _score) ~
          ("kind" -> Extraction.decompose(kind)) ~
          ("name" -> Extraction.decompose(name)) ~
          ("otherNames" -> Extraction.decompose(otherNames)) ~
          ("description" -> Extraction.decompose(description)) ~
          ("meta" -> Extraction.decompose(meta)) ~
          ("sources" -> sourcesList)
      }
    }
  }
}
