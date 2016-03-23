package org.nuata.core

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.source.Indexable
import com.sksamuel.elastic4s.{ElasticDsl, HitAs}
import org.elasticsearch.action.get.GetResponse
import org.json4s.JsonAST.{JDouble, JField, JObject, JString}
import org.json4s.jackson.JsonMethods._
import org.nuata.core.queries.{NameQuery, SuggestQuery}
import org.nuata.models.SearchSuggestion
import org.nuata.models.EsModel
import org.nuata.models.Label
import org.nuata.core.json.ESJackson._
import org.nuata.core.json.OptExtractors._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

/**
 * Created by nico on 03/03/16.
 */
class NodeRepository[T <: EsModel[T]](_type: String, _otherIndexName : Option[String] = None)(implicit mf: scala.reflect.Manifest[T], hitAs: HitAs[T], indexable: Indexable[T]) extends BaseRepository[T](_type.toString, _otherIndexName)(mf, hitAs, indexable) {

  val suggestFields = Array("suggest_name")

  def getSuggestions(searchOptions: SuggestQuery) : Future[Array[SearchSuggestion]] = {
    val suggestions = suggestFields.map { field =>
      completionSuggestion(field).field(s"labels.${searchOptions.lang.getOrElse("en")}.${field}").text(searchOptions.name).size(searchOptions.limit)
      //            fuzzyCompletionSuggestion(field).field(s"labels.${searchOptions.lang.getOrElse("en")}.${field}").text(searchOptions.name).size(searchOptions.limit)
    }
    ElasticSearch.client.execute(search in "nuata" suggestions (suggestions:_*) limit 0).map { res =>
      val json = parse(res.getSuggest.toString)

      suggestFields.flatMap { field =>
        val res: List[SearchSuggestion] = for { JObject(suggest) <- json \\ "options"
                                                JField("text", JString(id)) <- suggest
                                                JField("score", JDouble(score)) <- suggest
                                                JField("payload", JObject(payload)) <- suggest
                                                JField("type", JString(kind)) <- payload
                                                JField("name", JStringOpt(name)) <- payload
                                                JField("description", JStringOpt(description)) <- payload
        } yield SearchSuggestion(id, score, name, description, kind)
        res
      }
    }
  }

  def getLabels(response: GetResponse, lang: String): Option[Label] = {
    if(response.isExists) {
      val labels = response.getSource.get("labels").asInstanceOf[java.util.HashMap[String, java.util.HashMap[String, Any]]]
      val localizedLabel = labels.getOrDefault(lang, new java.util.HashMap[String, Any]())
      val name = Try(localizedLabel.get("name").toString).toOption
      val description = Try(localizedLabel.get("description").toString).toOption
      val aliases = Try(localizedLabel.get("aliases").asInstanceOf[Array[String]]).getOrElse(Array[String]())
      Some(Label(lang, name, description, aliases))
    } else {
      None
    }
  }

  def getNames(query: NameQuery): Future[Seq[Option[Label]]] = {
    val getQueries = query.id map { itemId => ElasticDsl.get id itemId from path }
    client.execute(multiget(getQueries)).map { res =>
      res.responses.map { multiGetResponses =>
        multiGetResponses.response.flatMap { response =>
          getLabels(response, query.lang)
        }
      }
    }
  }
}
