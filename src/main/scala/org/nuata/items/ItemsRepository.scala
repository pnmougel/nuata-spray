package org.nuata.items

import org.nuata.core.json.ESJackson._
import com.sksamuel.elastic4s.ElasticDsl._
import org.json4s.DefaultFormats
import org.nuata.core.{NodeRepository, BaseRepository}
import org.nuata.models._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by nico on 17/02/16.
 */
object ItemsRepository extends NodeRepository[Item]("items") {
  def countItemsWithAttribute(id: String): Future[Long] = {
    client.execute(search in path query termQuery("attributeIds", id)).map { res =>
      res.totalHits
    }
  }
}
