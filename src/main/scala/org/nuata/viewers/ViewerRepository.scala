package org.nuata.viewers

import org.nuata.core.BaseRepository
import org.nuata.models._
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.json4s.jackson.JsonMethods._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by nico on 15/03/16.
 */
object ViewerRepository extends BaseRepository[Viewer]("viewer") {

}