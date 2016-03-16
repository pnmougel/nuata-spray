package org.nuata.core

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.source.Indexable
import org.elasticsearch.action.get.GetResponse
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.ext.{EnumNameSerializer, EnumSerializer}
import org.json4s.jackson.JsonMethods._
import org.nuata.authentication.Role
import org.nuata.models.{Attribute, EsModel}
import org.nuata.models.queries.SearchQuery
import org.nuata.shared._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
 * Created by nico on 02/11/15.
 */

abstract class BaseRepository[T <: EsModel[T]](val `type`: String, val otherIndexName : Option[String] = None)(implicit mf: scala.reflect.Manifest[T], hitAs: HitAs[T], indexable: Indexable[T]) {
  val indexName = otherIndexName.getOrElse("nuata")
  val path = indexName / `type`
  val client = ElasticSearch.client

  implicit val formats = DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all + new EnumNameSerializer(Role) + new EnumSerializer(Role)

  def count : Future[Long] = client.execute { ElasticDsl.search in indexName limit 0 } map { res =>
    res.totalHits
  }

  def index(item: T, idOpt: Option[String] = None): Future[IndexResult] = {
    val indexQuery = (for(id <- idOpt) yield {
      ElasticDsl.index into path source item id id
    }).getOrElse(ElasticDsl.index into path source item)

    client.execute(indexQuery).andThen {
      case Success(x) => {

      }
      case Failure(x) => {
        println(x)
      }
    }
  }

  def bulkIndex(items: Seq[T], idOpt: Option[String] = None): Future[BulkResult] = {
    val indexQueries = items.map( item =>
      (for(id <- idOpt) yield {
        ElasticDsl.index into path source item id id
      }).getOrElse(ElasticDsl.index into path source item)
    )
    client.execute { bulk (indexQueries) }
  }

  def indexAndMap(item: T): Future[T] = {
    index(item) map { res => item.withId(res.getId) }
  }

  def indexAndMap(items: Seq[T]): Future[Seq[T]] = {
    bulkIndex(items) map { res =>
      items.zip(res.items).map( entry  => {
        entry._1.withId(entry._2.id)
      })
    }
  }

  def resultToEntity(res: GetResponse): T = {
    val js = parse(res.getSourceAsString).asInstanceOf[JObject]
    (js ~ ("_id" -> res.getId)).extract[T]
  }
  def resultToEntity(res: RichGetResponse): T = {
    val js = parse(res.sourceAsString).asInstanceOf[JObject]
    (js ~ ("_id" -> res.getId)).extract[T]
  }

  def byId(id: String): Future[T] = {
    client.execute {get id id from path}.map(resultToEntity)
  }

  def byIds(ids: Seq[String]): Future[Seq[Option[T]]] = {
    if(ids.isEmpty) {
      Future(List())
    } else {
      val getQueries = ids.map( id => get id id from path )
      client.execute { multiget(getQueries :_*)}.map( res => {
        res.responses.flatMap { item =>
          item.response.map { i =>
            if(i.isExists) {
              Some(resultToEntity(i))
            } else {
              None
            }
          }
        }
      })
    }
  }

  def byIdOpt(id: String): Future[Option[T]] = {
    client.execute {get id id from path}.map(item => {
      if(item.isExists) Some(resultToEntity(item)) else None
    })
  }

  def deleteById(id: String) : Future[Boolean] = {
    client.execute { delete id id from path }.map(res => {
      println(res.isFound)
      res.isFound
    })
  }

  def list(searchQuery: SearchQuery) : Future[(Long, Array[T])] = {
    val start = Math.max(0, searchQuery.limit * (searchQuery.page - 1))
    client.execute(ElasticDsl.search in path start start limit searchQuery.limit).map { res =>
      (res.totalHits, res.as[T])
    }
  }

  def update(id: String, f: T => T) : Future[Boolean] = {
    client.execute { get id id from path}.flatMap { item =>
      if(item.isExists) {
        val prevItem = parse(item.sourceAsString).extract[T]
        val newItem: T = f(prevItem)
        client.execute {
          ElasticDsl.update id id in path source newItem
        }.map { res =>
          true
        }
      } else {
        Future(false)
      }
    }
  }
}