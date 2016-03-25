package org.nuata.logging.requests

import org.nuata.models.EsModel

/**
 * Created by nico on 22/03/16.
 */
case class LogRequest(
                       _id: Option[String], _score: Option[Double],
                       createdAt: Long,
                       path: String,
                       pathParts: Array[String],
                       query: Map[String, List[String]],
                       method: String,
                       body: Option[String],
                       headers: List[String],
                       ip: Option[String],
                       status: Option[Int],
                       duration: Option[Long],
                       response: Option[String]) extends EsModel[LogRequest] {
  def withId(id: String) = this.copy(_id = Some(id))
}
