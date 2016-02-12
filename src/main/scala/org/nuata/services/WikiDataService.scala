package org.nuata.services

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.json4s.{DefaultFormats, Extraction}
import org.json4s.jackson.JsonMethods._
import org.nuata.models.wikidata.EsWikiDataItem
import org.nuata.services.routing.RouteRegistration
import org.nuata.shared.{ElasticSearch, Json4sProtocol}

import scala.concurrent.ExecutionContext.Implicits.global

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
    }
  }
}
