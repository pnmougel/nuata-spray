package org.nuata.services

import com.sksamuel.elastic4s.analyzers._
import com.sksamuel.elastic4s.ElasticDsl._
import org.nuata.services.routing.RouteRegistration
import org.nuata.shared.{Languages, ElasticSearch}
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.mappings._
import com.sksamuel.elastic4s.mappings.FieldType._
import spray.routing.HttpService

/**
 * Created by nico on 28/12/15.
 */
trait InitService extends RouteRegistration {
  registerRoute {
    path("init") {
      post {
        complete {
          initDb()
          "ok"
        }
      }
    }
  }

  def initDb() = {
    ElasticSearch.client.execute { deleteIndex("_all") }.await
    Thread.sleep(1000)

    val fields = List("name", "otherNames", "descriptions")

    //    val suggestField = "suggest" typed CompletionType indexAnalyzer("simple") searchAnalyzer("simple") payloads(true)

    val baseQuery = for(field <- fields) yield {
      val langFields = for((lang, langAnalyzer) <- Languages.analyzers.toList) yield {
        lang typed StringType analyzer langAnalyzer fields("raw" typed StringType analyzer "lowerKeywordAnalyzer")
      }
      //
      //       field inner (langFields : _*)
      field nested (langFields : _*)
    }

    val categoryIds = "categoryIds" typed StringType index "not_analyzed" fields("raw" typed StringType index "not_analyzed")
    val parentIds = "parentIds" typed StringType index "not_analyzed"
    val allParentIds = "allParentIds" typed StringType index "not_analyzed"
    val unitIds = "unitIds" typed StringType index "not_analyzed"
    val sourceIds = "sourceIds" typed StringType index "not_analyzed"

    //    name: Option[String], url: Option[String], authors: Seq[SourceAuthor]
    val sourceMapping = List(
      "kind" typed StringType index "not_analyzed",
      "name" typed StringType fields("raw" typed StringType index "not_analyzed"),
      "url" typed StringType index "not_analyzed",
      "authors" typed StringType index "not_analyzed")

    val increaseQueueSizeSetting = Map(
      "threadpool.search.type" -> "fixed",
      "threadpool.search.size" -> "7",
      "threadpool.search.size" -> "4000",

      "threadpool.index.type" -> "fixed",
      "threadpool.index.size" -> "7",
      "threadpool.index.size" -> "4000"
    )


    ElasticSearch.client.execute(cluster.persistentSettings(increaseQueueSizeSetting)).await
    ElasticSearch.client.execute(cluster.transientSettings(increaseQueueSizeSetting)).await

    ElasticSearch.client.execute {
      create.index("nuata").mappings(
        "category" as (sourceIds:: baseQuery),
        "dimension" as (allParentIds :: parentIds :: categoryIds :: sourceIds :: baseQuery),
        "unit" as (("kind" typed StringType index "not_analyzed") :: sourceIds :: baseQuery),
        "ooi" as (unitIds :: sourceIds:: baseQuery),
        "source" as sourceMapping,
        "fact" as (
          "value" typed DoubleType,
          "valueInt" typed LongType,
          "at" typed DateType,
          "sourceIds" typed StringType index "not_analyzed",
          "dimensionIds" typed StringType index "not_analyzed",
          "ooiIds" typed StringType index "not_analyzed"
          ),
        "query" as (
          "query" typed StringType,
          "categoryIds" typed StringType index "not_analyzed",
          "dimensionIds" typed StringType index "not_analyzed",
          "ooiIds" typed StringType index "not_analyzed",
          "unitIds" typed StringType index "not_analyzed",
          "ip" typed StringType,
          "at" typed DateType
          ),
        "user" as (
          "login" typed StringType index "not_analyzed",
          "email" typed StringType index "not_analyzed",
          "login" typed StringType index "not_analyzed",
          "hashedPassword" typed StringType index "not_analyzed",
          "token" typed StringType index "not_analyzed"
          )
      ).analysis(CustomAnalyzerDefinition(
        "lowerKeywordAnalyzer",
        KeywordTokenizer,
        LowercaseTokenFilter
      ))
    }.await
  }
}
