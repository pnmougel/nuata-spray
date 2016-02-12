package org.nuata.services

import java.util

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.json4s.{DefaultFormats, Extraction}
import org.json4s.jackson.JsonMethods._
import org.nuata.models.wikidata.EsWikiDataItem
import org.nuata.services.routing.RouteRegistration
import org.nuata.shared.{ElasticSearch, Json4sProtocol}
import spray.client.pipelining._
import spray.http.HttpHeaders.Accept
import spray.http.{MediaTypes, HttpResponse, HttpRequest}
import spray.httpx.encoding.{Deflate, Gzip}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConversions._
import scala.concurrent.Future

/**
 * Created by nico on 10/02/16.
 */
trait WikiDataService extends RouteRegistration with Json4sProtocol {

  registerRoute {
    (pathPrefix("wiki") & path("items" / Segment) & get) { query =>
      complete {
        ElasticSearch.client.execute( search in "wikidata" / "items" query {
          filter {
            nestedQuery("labels").query(
              filter(
                bool(
                  must(
                    Seq(
                      bool(
                        should(
                          Seq(
                            termQuery("labels.aliases.raw", query),
                            termQuery("labels.name.raw", query)
                          ) :_*
                        )
                      ),
                      termQuery("labels.lang", "en"))
                    : _*)
                  )
                )
            )
          }
        } size 10).map { res =>
          val hitJs = for(hit <- res.hits) yield {
            val item = hit.as[EsWikiDataItem]
            item.copy(labels = item.labels.filter(_.lang == "en"))
          }
          Extraction.decompose(hitJs)
        }
      }
    } ~
      (pathPrefix("wiki") & path("attributes" / Segment) & get) { query =>
      complete {
        ElasticSearch.client.execute( search in "wikidata" / "prop" query {
          filter {
            nestedQuery("labels").query(
              filter(
                bool(
                  must(
                    Seq(
                      termQuery("labels.name.raw", query),
                      termQuery("labels.lang", "en"))
                    : _*)
                  )
                )
            )
          }
        } size 10).map { res =>
          val hitJs = for(hit <- res.hits) yield {
            val item = hit.as[EsWikiDataItem]
            item.copy(labels = item.labels.filter(_.lang == "en"))
          }
          Extraction.decompose(hitJs)
        }
      }
    } ~
      (pathPrefix("wiki") & path("item" / Segment) & get) { q =>
      complete {
        ElasticSearch.client.execute( ElasticDsl.get id q from "wikidata" / "items" ).map { res =>
        implicit val formats = DefaultFormats
          val item = parse(res.sourceAsString)

          Extraction.decompose(item)
        }
      }
    } ~
      (pathPrefix("wiki") & path("attribute" / Segment) & get) { q =>
      complete {
        ElasticSearch.client.execute( ElasticDsl.get id q from "wikidata" / "prop" ).map { res =>
          Extraction.decompose(parse(res.sourceAsString))
        }
      }
    } ~
      (pathPrefix("wiki") & path("image" / Segment) & get) { imageName =>
      val pipeline: HttpRequest => Future[CommonsMediaInfo] = (
        addHeader(Accept(MediaTypes.`application/json`))
          ~> encode(Gzip)
          ~> sendReceive
          ~> decode(Deflate)
           ~> unmarshal[CommonsMediaInfo])
      val future = pipeline(Get(s"https://commons.wikimedia.org/w/api.php?action=query&format=json&list=allimages&aiprefix=${imageName}"))

      onSuccess(future) {
        case foo: CommonsMediaInfo => {
          complete(Extraction.decompose(foo.query.allimages.headOption))
        }
      }
//      complete {
//      https://commons.wikimedia.org/w/api.php?action=query&list=allimages&aiprefix=
//        "ok"
//      }
    } ~
      (pathPrefix("wiki") & path("name" / Segment / Segment) & get) { case (lang, id) =>
      complete {
        val index = if(id.startsWith("Q")) "items" else "prop"
        ElasticSearch.client.execute( ElasticDsl.get id id from "wikidata" / index).map { res =>
          if(res.isExists) {
            val labels = res.source.get("labels").asInstanceOf[util.ArrayList[java.util.HashMap[String, _]]]
            val label = for(label <- labels; if(label.getOrElse("lang", "") == lang)) yield {
              Map(
                "name" -> label.getOrElse("name", "-"),
                "description" -> label.getOrElse("description", "-")
              )
            }
            Extraction.decompose(label.headOption)
          } else {
            Extraction.decompose(Map("name" -> "-", "description" -> "-"))
          }
        }
      }
    }
  }
}

case class CommonsMediaImageInfo(name: String, timestamp: String, url: String)

case class CommonsMediaImagesInfo(allimages: Array[CommonsMediaImageInfo])

case class CommonsMediaInfo(query: CommonsMediaImagesInfo)