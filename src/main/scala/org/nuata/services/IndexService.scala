package org.nuata.services

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.nuata.authentication.Authenticator
import org.nuata.models._
import org.nuata.repositories.DimensionRepository
import org.nuata.services.routing.RouteRegistration
import org.nuata.shared.{ElasticSearch, Json4sProtocol}
import spray.routing.{Directive1, HttpService}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future._

/**
 * Created by nico on 28/12/15.
 */
trait IndexService extends RouteRegistration with Json4sProtocol with Authenticator {

  val nameToDirective = Map[String, Directive1[_]](
    "unit" -> entity(as[Array[Unit]]),
    "dimension" -> entity(as[Array[Dimension]]),
    "category" -> entity(as[Array[Category]]),
    "source" -> entity(as[Array[Source]]),
    "fact" -> entity(as[Array[Fact]]),
    "ooi" -> entity(as[Array[Ooi]]))

  registerRoute {
    (for((name, directive) <- nameToDirective) yield {
    (path(name) & post & directive ) { items =>
      val itemsSeq = items.asInstanceOf[Array[_]]
      val indexQueries = itemsSeq.map( item => index into "nuata" / name source item)
      complete {

        ElasticSearch.client.execute(bulk (indexQueries) ).map( results => {
          // val newChildrenForParent = mutable.HashMap[Future[Dimension], Vector[String]]()
//          val newChildrenForParent = mutable.HashMap[Dimension, Vector[String]]()

          val allParentsForItem = mutable.HashMap[String, List[String]]()

          val newIds = for((item, i) <- results.getItems.zipWithIndex) yield {
            item.getType match {
              case "dimension" => {
                val dimensionItem = itemsSeq(i).asInstanceOf[Dimension]
                val curDimensionId = item.getId
                allParentsForItem(curDimensionId) = List[String]()
                for(parentId <- dimensionItem.parentIds) {
                  // val parent = DimensionRepository.byId(parentId)
                  val parent = DimensionRepository.byId(parentId).await
                  allParentsForItem(curDimensionId) = parentId :: allParentsForItem(curDimensionId) ::: parent.allParentIds
//                  parent.allParentIds
//                  newChildrenForParent(parent) = newChildrenForParent.getOrElse(parent, Vector[String]()) :+ item.getId
                }
              }
              case _ => {}
            }
            item.getId
          }

//          if(newChildrenForParent.nonEmpty) {
//            ElasticSearch.client.execute(bulk {
//            for((parent, children) <- newChildrenForParent.toList) yield {
//              update id parent._id.get in "nuata/dimension" doc("childrenIds" -> (parent.childrenIds ::: children.toList))
//            }}).await
//          }
          if(allParentsForItem.nonEmpty) {
          ElasticSearch.client.execute(bulk {
            for((dimId, allParents) <- allParentsForItem.toList) yield {
              update id dimId in "nuata/dimension" doc("allParentIds" -> allParents)
            }}).await
          }

//          sequence(for((parentFuture, children) <- newChildrenForParent.toList) yield {
//            parentFuture.map { parent =>
//              update id parent._id.get in "nuata/dimension" doc("childrenIds" -> (parent.childrenIds ::: children.toList))
//            }
//          }).map { requests =>
//            ElasticSearch.client.execute(bulk { requests })
//          }
          newIds
        })
      }
    }
  }).reduce((a, b) => { a ~ b })
}
}
