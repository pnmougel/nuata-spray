package org.nuata.models

import org.json4s.JsonAST.JValue

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by nico on 26/11/15.
 */
trait JsonSerializable {
  def toJson(level: Int = -1): Future[JValue]

  def toJsonSeq(items: Future[Seq[JsonSerializable]], level: Int = -1) = items.flatMap { items =>
    Future.sequence(items.map(item => item.toJson(level)))
  }
}
