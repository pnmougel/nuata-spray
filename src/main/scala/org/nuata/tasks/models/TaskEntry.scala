package org.nuata.tasks.models

import java.util.Date

import org.nuata.models.EsModel

/**
 * Created by nico on 17/03/16.
 */
case class TaskEntry(_id: Option[String], _score: Option[Double], name: String, status: String, percentage: Double = 0, createdAt: Option[Date], options: Map[String, Any], doing: String = "", isStopped: Boolean = false)
  extends EsModel[TaskEntry] {
  def withId(id: String) = this.copy(_id = Some(id))
}
