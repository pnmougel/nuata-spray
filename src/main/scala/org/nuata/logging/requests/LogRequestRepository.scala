package org.nuata.logging.requests

import com.sksamuel.elastic4s.TermAggregationDefinition
import com.sksamuel.elastic4s.mappings.FieldType._
import org.elasticsearch.search.aggregations.bucket.terms.InternalTerms.Bucket
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import org.nuata.core.BaseRepository
import org.nuata.models._
import com.sksamuel.elastic4s.ElasticDsl._
import org.nuata.shared.json.ESJackson._
import org.json4s.jackson.JsonMethods._
import scala.collection.JavaConversions._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by nico on 22/03/16.
 */
object LogRequestRepository extends BaseRepository[LogRequest]("request", Some("logs")) {
  def deleteIndex = {
    client.execute(delete index "logs").map { res =>
      client.execute(create index "logs" mappings (
        mapping("request").fields(
          field("created_at") typed LongType,
          field("path") typed StringType index "not_analyzed",
          field("path_parts") typed StringType index "not_analyzed",
          field("query") typed ObjectType,
          field("method") typed StringType index "not_analyzed",
          field("body") typed StringType index "not_analyzed",
          field("headers") typed StringType index "not_analyzed",
          field("ip") typed IpType index "not_analyzed",
          field("duration") typed LongType,
          field("response") typed StringType index "not_analyzed",
          field("status") typed IntegerType
          )
        )
      )
    }
  }

  def getValues: Future[Map[String, Map[String, Long]]] = {
    client.execute(
      search in path aggregations (
        aggregation terms "status" field "status",
        aggregation terms "path" field "path",
        aggregation terms "method" field "method",
        aggregation terms "ip" field "ip"
        )
    ).map { res =>
      def getMap(term: String) = {
        Map[String, Long](res.aggregations.get[Terms](term).getBuckets.toList.map { bucket =>
          (bucket.getKeyAsString, bucket.getDocCount)
        } : _*)
      }
      Map(
        "status" -> getMap("status"),
        "path" -> getMap("path"),
        "method" -> getMap("method"),
        "ip" -> getMap("ip"))
    }
  }
}