package org.nuata.tasks

import org.nuata.core.BaseRepository
import org.nuata.core.json.ESJackson._
import org.nuata.tasks.models.TaskEntry

/**
 * Created by nico on 24/02/16.
 */
object TaskRepository extends BaseRepository[TaskEntry]("task", Some("tasks"))