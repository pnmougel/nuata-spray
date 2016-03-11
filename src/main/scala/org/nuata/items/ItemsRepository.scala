package org.nuata.items

import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.json4s.DefaultFormats
import org.nuata.core.{NodeRepository, BaseRepository}
import org.nuata.models._

/**
 * Created by nico on 17/02/16.
 */
object ItemsRepository extends NodeRepository[Item]("items") {
}
