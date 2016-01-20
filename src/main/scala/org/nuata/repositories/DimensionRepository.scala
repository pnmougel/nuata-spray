package org.nuata.repositories

import org.elasticsearch.action.search.SearchResponse
import org.json4s._
import org.nuata.models.Dimension
import com.sksamuel.elastic4s.ElasticDsl._
import org.nuata.shared.ElasticSearch
import scala.concurrent.ExecutionContext.Implicits.global
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

import scala.concurrent.Future
import scala.concurrent.Future._

/**
 * Created by nico on 02/11/15.
 */
object DimensionRepository extends BaseRepository[Dimension]("dimension") {
  protected def jsToInstance(jValue: JValue) = jValue.extract[Dimension]

  def resultToEntity(res: SearchResponse) = res.as[Dimension]

  def removeDependency(dimensionId: String, dependencyId: String, dependencyType: String) = {
    byIdOpt(dimensionId).map(dimensionOpt => {
      for(dimension <- dimensionOpt) yield {
        val prevList = dependencyType match {
          case "parentIds" => dimension.parentIds
          case "categoryIds" => dimension.categoryIds
          case _ => List[String]()
        }
        val newIds = prevList.filter(_ != dependencyId)
        client.execute {
          update id dimensionId in path docAsUpsert (dependencyType -> newIds)
        }
      }
    })
  }

  def children(ids: Seq[String], maxLevel: Int = 1, curLevel: Int = 1) : Future[Vector[Dimension]] = {
    DimensionRepository.join("parentIds", ids, 100000).flatMap( res => {
      val allNewIds = res.getHits.hits().map(_.getId)
      if(allNewIds.isEmpty || curLevel >= maxLevel) {
        Future(Vector())
      } else {
        sequence(allNewIds.sliding(1000).toVector.map( idSlice => {
          children(idSlice, maxLevel, curLevel + 1)
        })).map(it => {
          it.flatten ++ res.as[Dimension]
        })
      }
    })
  }

  def getChildren2(id: String) : Future[Array[Dimension]] = {
    client.execute(search in path query { termsQuery("allParentIds", id) } limit 100000).map( res => {
      resultToEntity(res)
    })
  }

  def getChildren(id: String) : Future[Long] = {
    client.execute(search in path query { termsQuery("allParentIds", id) } limit 100).map( res => {
      res.getHits.getTotalHits
    })
  }
//
//  def children2(id: String, maxLevel: Int = 1, curLevel: Int = 1) : Future[List[Dimension]] = {
//    if(curLevel >= maxLevel) {
//      Future(List())
//    } else {
//      DimensionRepository.byId(id).flatMap { dimension =>
//        sequence(dimension.childrenIds.map( childId => children2(childId, maxLevel, curLevel + 1))).map(it => it.flatten :+ dimension)
//      }
//    }
//  }
//
//  def children3(dimension: Dimension, maxLevel: Int = 1, curLevel: Int = 1) : Future[List[Dimension]] = {
//    if(curLevel >= maxLevel) {
//      Future(List())
//    } else {
//      DimensionRepository.byIds(dimension.childrenIds).flatMap( children => {
//        sequence(children.map { child =>
//          children3(child, maxLevel, curLevel + 1)
//        }).map(it => it.flatten :+ dimension)
//      })
//    }
//  }
//
//
//  def children4(id: String, maxLevel: Int = 1, curLevel: Int = 1) : Future[List[String]] = {
//    if(curLevel >= maxLevel) {
//      Future(List())
//    } else {
//      DimensionRepository.byIdsFast(List(id)).flatMap( children => {
//        sequence(children.map { child =>
//          children4(child, maxLevel, curLevel + 1)
//        }).map(it => it.flatten :+ id)
//      })
//    }
//  }


}