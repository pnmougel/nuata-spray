//package org.nuata.attributes
//
//import com.sksamuel.elastic4s.ElasticDsl
//import com.sksamuel.elastic4s.ElasticDsl._
//import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
//import org.json4s.Extraction._
//import org.nuata.attributes.queries.{NameQuery, AttributeQuery}
//import org.nuata.graphmodel.Attribute
//import org.nuata.services.routing.RouteProvider
//import org.nuata.shared.{ElasticSearch, Json4sProtocol, QueryParams}
//import spray.routing.MalformedQueryParamRejection
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.collection.JavaConversions._
//
///**
// * Created by nico on 05/03/16.
// */
//object AttributeRoute extends RouteProvider with Json4sProtocol {
//  val client = ElasticSearch.client
//
//  def route(implicit settings: spray.routing.RoutingSettings, refFactory: akka.actor.ActorRefFactory) = {
//    (pathPrefix("attribute") & path("search") & parameterMultiMap & get) { params =>
//      QueryParams.as[AttributeQuery](params) match {
//        case Left(errors) => reject(MalformedQueryParamRejection(errors._1, errors._2))
//        case Right(query) => {
//          val nameQuery = if(query.name.trim.isEmpty) {
//            matchAllQuery
//          } else {
////            nestedQuery(s"labels.${query.lang}").query(matchQuery(s"labels.${query.lang}.name", query.name))
//            nestedQuery(s"labels.${query.lang}").query(prefixQuery(s"labels.${query.lang}.name", query.name))
//          }
//
//          val dataTypeQuery = if(query.valueType.trim.isEmpty) {
//            matchAllQuery
//          } else {
//            termQuery("valueType", query.valueType)
//          }
//          val filterQuery = bool(must(nameQuery, dataTypeQuery))
//
//          val future = client.execute(search in "nuata" / "attributes" query filterQuery start (query.page * query.limit) limit query.limit).map { res =>
//            decompose(Map("nbItems" -> res.totalHits,
//                "items" -> res.as[Attribute])
//            )
//          }
//          complete(future)
//        }
//      }
//    } ~ (pathPrefix("attribute") & path("name") & parameterMultiMap & get) { params =>
//      QueryParams.as[NameQuery](params) match {
//        case Left(errors) => reject(MalformedQueryParamRejection(errors._1, errors._2))
//        case Right(query) => {
//          val getQueries = query.id map { itemId =>
//            ElasticDsl.get id itemId from "nuata" / "attributes"
//          }
//          val future = client.execute(multiget(getQueries)).map { res =>
//            res.responses.map { multiGetResponses =>
//              multiGetResponses.response.map { response =>
//                val labels = response.getSourceAsMap().get("labels").asInstanceOf[java.util.HashMap[String, java.util.HashMap[String, Any]]]
//                val localizedLabel = labels.getOrElse(query.lang, new java.util.HashMap[String, Any]())
//                val name = if(localizedLabel.contains("name")) {
//                  Some(localizedLabel.get("name").toString)
//                } else {
//                  None
//                }
//                val description = if(localizedLabel.contains("description")) {
//                  Some(localizedLabel.get("description").toString)
//                } else {
//                  None
//                }
//                Map("id" -> response.getId, "name" -> name, "description" -> description)
//              }
//            }
//          }
//          complete(future)
//        }
//      }
//    }
//  }
//}
