package org.nuata.shared

import java.util.UUID

import com.sksamuel.elastic4s.ElasticDsl._

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by nico on 20/02/16.
 */
object UUIDGenerator {
  var uuids = mutable.HashMap[String, Vector[String]]()
  val indexName = Settings.getString("elasticsearch.index")

  val nbUUIDsToGenerate = 1024

  def generateUUIDs(docType: String): Future[Seq[String]] = {
    var i = 0
    val uuidSet = mutable.HashSet[String]()
    while(i != nbUUIDsToGenerate) {
      i += 1
      uuidSet.add(UUID.randomUUID().toString)
    }
    val uuids = uuidSet.toArray
    val queries = for(uuid <- uuids) yield get id id from indexName / docType

    ElasticSearch.client.execute(multiget(queries: _*)).map { res =>
      res.responses.zip(uuids).filter { case (a, b) => {
        !a.response.map(_.isExists).getOrElse(true)
        }
      }.map(_._2)
    }
  }

  def getUUID(docType: String): Future[String] = {
    if(uuids(docType).isEmpty) {
      generateUUIDs(docType).map { res =>
        uuids(docType) = res.toVector
        uuids(docType).head
      }
    } else {
      Future {
        uuids(docType).head
      }
    }
  }
}
