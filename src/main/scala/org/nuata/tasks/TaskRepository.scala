package org.nuata.tasks

import com.sksamuel.elastic4s.{MatchAllQueryDefinition, ElasticDsl}
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.nuata.attributes.queries.AttributeSearchQuery
import org.nuata.core.{BaseRepository, NodeRepository}
import org.nuata.models.Attribute
import org.nuata.tasks.models.TaskEntry
import org.nuata.tasks.queries.TaskQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by nico on 24/02/16.
 */
object TaskRepository extends BaseRepository[TaskEntry]("task") {
}
