package org.nuata.repositories

import java.util
import java.util.concurrent.TimeUnit

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.source.Indexable
import com.sksamuel.elastic4s.{HitAs, SearchType, ElasticDsl}
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.count.CountResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchResponse
import org.json4s._
import org.json4s.ext.{EnumSerializer, EnumNameSerializer}
import org.nuata.authentication.Role
import org.nuata.models.{EsModel, Dimension, JsonSerializable}
import org.nuata.models.queries.SearchQuery
import org.nuata.shared._

import scala.collection.mutable
import scala.concurrent.Future
import org.json4s.JsonDSL._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.duration._

import com.sksamuel.elastic4s.{HitAs, ElasticDsl}
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.count.CountResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.search.SearchResponse
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.ext.{EnumNameSerializer, EnumSerializer}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Created by nico on 02/11/15.
 */

abstract class BaseRepository[T <: EsModel[T]](val `type`: String)(implicit mf: scala.reflect.Manifest[T], hitAs: HitAs[T], indexable: Indexable[T]) {
  val path = "nuata" / `type`
  val client = ElasticSearch.client

  implicit val formats = DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all + new EnumNameSerializer(Role) + new EnumSerializer(Role)

  def count = client.execute { ElasticDsl.count from "nuata" types `type` }



  def index(item: T): Future[IndexResponse] = {
    client.execute { ElasticDsl.index into path source item }
  }
  def index(items: Seq[T]): Future[BulkResponse] = {
    val indexQueries = items.map( item => ElasticDsl.index into path source item)
    client.execute { bulk (indexQueries) }
  }

  def indexAndMap(item: T): Future[T] = {
    index(item) map { res => item.withId(res.getId) }
  }
  def indexAndMap(items: Seq[T]): Future[Seq[T]] = {
    index(items) map { res =>
      items.zip(res.getItems).map( entry  => {
        entry._1.withId(entry._2.getId)
      })
    }
  }


  def resultToEntity(res: GetResponse): T = {
    val js = org.json4s.jackson.JsonMethods.parse(res.getSourceAsString).asInstanceOf[JObject]
    (js ~ ("_id" -> res.getId)).extract[T]
  }

  def byId(id: String): Future[T] = {
    client.execute {get id id from path}.map(resultToEntity)
  }

  def byIds(ids: List[String]): Future[List[T]] = {
    if(ids.isEmpty) {
      Future(List())
    } else {
      val getQueries = ids.map( id => get id id from path )
      client.execute { multiget(getQueries :_*)}.map( res => {
        res.getResponses.toList.flatMap( r => {
          if(r.isFailed) {
            None
          } else {
            Some(resultToEntity(r.getResponse))
          }
        })
      })
    }
  }

  def byIdOpt(id: String): Future[Option[T]] = {
    client.execute {get id id from path}.map(item => {
      if(item.isExists) Some(resultToEntity(item)) else None
    })
  }

  def doSearch(searchOptions: SearchQuery) : Future[(CountResponse, SearchResponse)] = {
    val nameLower = searchOptions.name.toLowerCase

    val nameQuery = nestedQuery("otherNames").query( bool {
      should { for(lang <- Languages.available) yield {
        searchOptions.nameOperation match {
          case NameOperations.StartsWith => filteredQuery filter prefixFilter(s"otherNames.$lang.raw", nameLower)
          case NameOperations.Match => matchQuery(s"otherNames.$lang", nameLower)
          case NameOperations.Exact => filteredQuery filter termFilter(s"otherNames.$lang.raw", nameLower)
          case _ => filteredQuery filter prefixFilter(s"otherNames.$lang.raw", nameLower)
        }
      }}
    })

    val idsQuery = for((field, itemIds) <- searchOptions.filters.toList) yield {
      must(itemIds.map(id => termQuery(field, id)))
    }

    val inParentQuery = must(termsQuery("allParentIds", searchOptions.hasParentId : _*))

    val filterQuery = if(!searchOptions.hasParentId.isEmpty) {
      bool { must { inParentQuery :: nameQuery :: idsQuery} }
    } else {
      bool { must { nameQuery :: idsQuery} }
    }

    val countQuery = ElasticDsl.count.from(path).where(filterQuery)
    val searchQuery = search in path query filterQuery

    client.execute(countQuery).flatMap( countRes => {
      client.execute { searchQuery start searchOptions.start limit searchOptions.limit }.map(items => {
        (countRes, items)
      })
    })
  }

  def join(fieldName: String, ids: Seq[String], limit: Int = 1000) = {
    client.execute(search in path query { termsQuery(fieldName, ids :_*) } limit limit)
  }

  def searchAndExpand(searchOptions: SearchQuery) = {
    doSearch(searchOptions).flatMap( res => {
      val (count, search) = res
      Future.sequence(search.as[T].toList.map( item => {
        item.asInstanceOf[JsonSerializable].toJson(searchOptions.expand)
      })).map( jValues => {
        Map("nbItems" -> count.getCount,
          "items" -> jValues)
      })
    })
  }

  def deleteById(id: String) : Future[Boolean] = {
    client.execute { delete id id from path }.map(res => {
      res.isFound
    })
  }

  def list() = {
    var i = 0
    val dimensionIdToChildByCategory = mutable.HashMap[String, mutable.HashMap[String, List[(Int, Dimension)]]]()

    for(res <- client.iterateSearch(search in path query "*" size 10000)(1.minutes)) {
      val items = res.as[T].asInstanceOf[Array[Dimension]]
      items.map(item => {
        for(parentId <- item.parentIds; categoryId <- item.categoryIds) {
//          dimensionIdToChildByCategory(parentId)(categoryId) = List((1, item))
        }
      })
      i += 1
      println(i)
    }

//    for(res <- client.iterate(search in path query query("*") size 1000)(Duration.create(1, TimeUnit.MINUTES)) {
//    }
//    client.execute { search in path query query("*") searchType SearchType.Scan scroll "1m" size 1000 }.map(item => {
//      val scrollId = item.getScrollId
//      println(scrollId)
//
//      client.execute( search scroll scrollId ).map(res => {
////        println("1: " + item.getHits.getAt(0).getId)
//        println(res.getScrollId)
//        println(res.getHits.getTotalHits)
//
//        println(res.getHits.hits().size)
//        client.execute( search scroll scrollId ).map(res => {
//          println(res.getHits.getTotalHits)
//          println(res.getHits.hits().size)
//        })
//      })
//      resultToEntity(item)
//    })
  }
}
