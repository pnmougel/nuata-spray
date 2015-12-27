package org.nuata.models

import org.json4s.JsonDSL._
import org.json4s._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
 * Created by nico on 30/10/15.
 */
case class Source(_id: Option[String],
                       _score: Option[Double],
                       name: String,
                       url: Option[String],
                       kind: String,
                       authors: List[String],
                       meta: Option[Map[String, _]])
  extends BaseModel(_id, _score)
  with JsonSerializable {

  implicit val formats = DefaultFormats

  def toJson(level: Int = -1): Future[JObject] = {
    Future(
      ("_id" -> _id) ~
        ("_score" -> _score) ~
        ("name" -> name) ~
        ("url" -> Extraction.decompose(url)) ~
        ("kind" -> kind) ~
        ("meta" -> Extraction.decompose(meta)) ~
        ("authors" -> Extraction.decompose(authors)))
  }
}
