package org.nuata.repositories.commons

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.count.CountResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.search.SearchResponse
import org.json4s.JsonDSL._
import org.json4s._
import org.nuata.models.LocalizedNamedModel
import org.nuata.repositories.{NameOperations, SearchOptions, BaseRepository}
import org.nuata.shared.{ElasticSearch, Languages}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by nico on 02/11/15.
 */
abstract class LocalizedNamedItemRepository[T](val `type`: String)
  extends BaseRepository(`type`) {
  /**
   * Convert a search response to an array of model instance
   * @param res
   * @return
   */
  def resultToEntity(res: SearchResponse) : Array[T]

  def resultToEntity(res: GetResponse): T = {
    val js = org.json4s.jackson.JsonMethods.parse(res.getSourceAsString).asInstanceOf[JObject]
    jsToInstance(js ~ ("_id" -> res.getId))
  }

  protected def jsToInstance(jValue: JValue): T

  def byId(id: String): Future[T] = {
    ElasticSearch.client.execute {get id id from path}.map(resultToEntity)
  }

  def byIdOpt(id: String): Future[Option[T]] = {
    ElasticSearch.client.execute {get id id from path}.map(item => {
      if(item.isExists) Some(resultToEntity(item)) else None
    })
  }

  def doSearch(searchOptions: SearchOptions) : Future[(CountResponse, SearchResponse)] = {
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

    val filterQuery = bool { must { nameQuery :: idsQuery } }
    val countQuery = ElasticDsl.count.from(path).where(filterQuery)
    val searchQuery = search in path query filterQuery

    ElasticSearch.client.execute(countQuery).flatMap( countRes => {
      ElasticSearch.client.execute { searchQuery start searchOptions.start limit searchOptions.limit }.map(items => {
        (countRes, items)
      })
    })
  }

  def join(fieldName: String, ids: List[String]) = {
    ElasticSearch.client.execute(search in path query { termsQuery(fieldName, ids :_*) })
  }

  def searchAndExpand(searchOptions: SearchOptions) = {
    doSearch(searchOptions).flatMap( res => {
      val (count, search) = res
      Future.sequence(resultToEntity(search).toList.map( item => {
        item.asInstanceOf[LocalizedNamedModel].toJson(searchOptions.expand)
      })).map( jValues => {
        Map("nbItems" -> count.getCount,
            "items" -> jValues)
      })
    })
  }

  def indexItems(models: List[LocalizedNamedModel]) : Future[Array[String]] = {
    val indexQueries = models.map( model => index into path fields model.getIndexQuery)
    client.execute(bulk (indexQueries) ).map( results => {
      results.getItems.map( _.getId )
    })
  }
}
