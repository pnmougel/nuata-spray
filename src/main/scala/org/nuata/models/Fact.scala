package org.nuata.models

import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.{Extraction, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.nuata.repositories._

/**
 * Created by nico on 02/11/15.
 */
case class Fact(_id: Option[String], _score: Option[Double],
                     ooiIds: List[String], dimensionIds: List[String], sourceIds: List[String],
                     at: Option[java.util.Date],
                     value: Option[Double], valueInt: Option[Long],
                     meta: Map[String, _])
  extends BaseModel(_id, _score)
  with JsonSerializable {

  implicit val formats = DefaultFormats

  val oois = Future.sequence(for(ooiId <- ooiIds) yield { OoiRepository.byId(ooiId) })
  val dimensions = Future.sequence(for(dimensionId <- dimensionIds) yield { DimensionRepository.byId(dimensionId) })
  val sources = Future.sequence(for(sourceId <- sourceIds) yield { SourceRepository.byId(sourceId) })

  def toJson(level: Int = -1): Future[JObject] = {
    if(level == 0) {
      Future(("value" -> value) ~
        ("valueInt" -> valueInt) ~
        ("oois" -> ooiIds) ~
        ("dimensions" -> dimensionIds) ~
        ("meta" -> Extraction.decompose(meta)) ~
        ("sources" -> sourceIds))
    } else {
      for(ooisJson <- toJsonSeq(oois, level - 1);
          dimensionsJson <- toJsonSeq(dimensions, level - 1);
          sourceJson <- toJsonSeq(sources, level - 1)) yield {
        ("value" -> value) ~
          ("valueInt" -> valueInt) ~
          ("oois" -> ooisJson) ~
          ("dimensions" -> dimensionsJson) ~
          ("meta" -> Extraction.decompose(meta)) ~
          ("sources" -> sourceJson)
      }
    }

  }
}
