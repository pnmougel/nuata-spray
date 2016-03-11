package org.nuata.services.commonsmedia

import org.nuata.core.BaseRepository
import org.nuata.services.commonsmedia.model.MediaInfo
import org.nuata.shared.StringHash
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by nico on 25/02/16.
 */
object CommonsMediaRepository extends BaseRepository[MediaInfo]("media", Some("commonsmedia")) {
  def getMediaInfo(fileName: String) = {
    val id = StringHash.getHash(fileName)
    byIdOpt(id)
  }

  def setMediaInfo(fileName: String, mediaInfo: MediaInfo): Future[Boolean] = {
    val id = StringHash.getHash(fileName)
    index(mediaInfo, Some(id)).map { x =>
      x.isCreated
    }
  }
}
