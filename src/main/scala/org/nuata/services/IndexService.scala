package org.nuata.services

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.nuata.models._
import org.nuata.shared.{ElasticSearch, Json4sProtocol}
import spray.routing.{Directive1, HttpService}

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by nico on 28/12/15.
 */
trait IndexService extends HttpService with Json4sProtocol {

//  val nameToDirective = Map[String, Directive1[_]](
//    "unit" -> entity(as[Unit]),
//    "dimension" -> entity(as[Dimension]),
//    "category" -> entity(as[Category]),
//    "source" -> entity(as[Source]),
//    "fact" -> entity(as[Fact]),
//    "ooi" -> entity(as[Ooi]))

  val nameToDirective = Map[String, Directive1[_]](
    "unit" -> entity(as[List[Unit]]),
    "dimension" -> entity(as[List[Dimension]]),
    "category" -> entity(as[List[Category]]),
    "source" -> entity(as[List[Source]]),
    "fact" -> entity(as[List[Fact]]),
    "ooi" -> entity(as[List[Ooi]]))

  val indexRoutes = (for((name, directive) <- nameToDirective) yield {
    path(name) {
      post {
        directive { items =>
          val itemsSeq = items.asInstanceOf[List[_]]
          val indexQueries = itemsSeq.map( item => index into "nuata" / name source item)
          complete {
            ElasticSearch.client.execute(bulk (indexQueries) ).map( results => {
              results.getItems.map( _.getId )
            })

            /*
            ElasticSearch.client.execute {
              index into "nuata" / name source item
            }.map(res => {
              Extraction.decompose(res.getId)
            })
            */
          }
        }
      }
    }
  }).reduce((a, b) => { a ~ b })
}
