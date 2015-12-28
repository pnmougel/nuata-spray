package org.nuata.models

import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s._
import org.nuata.repositories._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
 * Created by nico on 30/10/15.
 */
case class Dimension(_id: Option[String],
                          _score: Option[Double],
                          name: Map[String, String],
                          otherNames: Map[String, List[String]],
                          description: Map[String, String],
                          categoryIds: List[String],
                          parentIds: List[String],
                          sourceIds: List[String],
                          meta: Map[String, _])
  extends BaseModel(_id, _score)
  with JsonSerializable {

  def toJson(level: Int = -1) : Future[JObject] = {
    implicit val formats = DefaultFormats

    if(level == 0) {
      Future(("_id" -> _id) ~
        ("_score" -> _score) ~
        ("name" -> Extraction.decompose(name)) ~
        ("otherNames" -> Extraction.decompose(otherNames)) ~
        ("description" -> Extraction.decompose(description)) ~
        ("categories" -> categoryIds) ~
        ("parents" -> parentIds) ~
        ("meta" -> Extraction.decompose(meta)) ~
        ("sources" -> sourceIds))
    } else {
      val categories = Future.sequence(for(categoryId <- categoryIds) yield { CategoryRepository.byId(categoryId) })
      val parents = Future.sequence(for(parentId <- parentIds) yield { DimensionRepository.byId(parentId) })
      val sources = Future.sequence(for(sourceId <- sourceIds) yield { SourceRepository.byId(sourceId) })

      for(categoriesList <- toJsonSeq(categories, level - 1);
          parentsList <- toJsonSeq(parents, level - 1);
          sourcesList <- toJsonSeq(sources, level - 1)) yield {
        ("_id" -> _id) ~
          ("_score" -> _score) ~
          ("name" -> Extraction.decompose(name)) ~
          ("otherNames" -> Extraction.decompose(otherNames)) ~
          ("description" -> Extraction.decompose(description)) ~
          ("categories" -> categoriesList) ~
          ("parents" -> parentsList) ~
          ("meta" -> Extraction.decompose(meta)) ~
          ("sources" -> sourcesList)
      }
    }
  }
}
