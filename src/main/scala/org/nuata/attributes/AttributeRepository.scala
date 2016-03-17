package org.nuata.attributes

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.json4s.jackson.JsonMethods._
import org.nuata.attributes.queries.AttributeSearchQuery
import org.nuata.core.NodeRepository
import org.nuata.models.Attribute

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by nico on 24/02/16.
 */
object AttributeRepository extends NodeRepository[Attribute]("attributes") {
//
//  def attributeSearch(query: AttributeSearchQuery): Future[(Long, Array[Attribute])] = {
//    val nameQuery = if(query.name.trim.isEmpty) {
//      matchAllQuery
//    } else {
////      nestedQuery(s"labels.${query.lang}").query(matchQuery(s"labels.${query.lang}.name", query.name))
//      nestedQuery(s"labels.${query.lang}").query(prefixQuery(s"labels.${query.lang}.name", query.name))
//    }
//
//    val instanceOfQuery = query.instanceOf.map { id =>
//      if(id.isEmpty) {
//        matchAllQuery
//      } else {
//        termQuery("instancesOf", id)
//      }
//    }.getOrElse(matchAllQuery)
//
//    val dataTypeQuery = if(query.valueType.trim.isEmpty) {
//      matchAllQuery
//    } else {
//      termQuery("valueType", query.valueType)
//    }
//    val filterQuery = bool(must(nameQuery, dataTypeQuery, instanceOfQuery))
//
//    val start = Math.max(0, (query.page - 1) * query.limit)
//
//    client.execute(ElasticDsl.search in path query filterQuery start start limit query.limit).map { res =>
//      (res.totalHits, res.as[Attribute])
//    }
//  }
}
