package org.nuata.actors

import akka.actor.Actor
import org.json4s.{Extraction, MappingException}
import org.nuata.services.RouteService
import spray.http.{MediaType, HttpEntity, ContentType, HttpResponse}
import spray.http.StatusCodes._
import spray.routing.{MalformedRequestContentRejection, RejectionHandler}

/**
 * Created by nico on 23/12/15.
 */
class HttpActor extends Actor with RouteService {
  def actorRefFactory = context

  implicit def myRejectionHandler = RejectionHandler {
    case MalformedRequestContentRejection(errorMsg, e) :: _ =>
//      println(errorMsg)
//      println(e)
//      for(foo <- e) {
//        println(foo.getClass)
//        val mappingException = foo.asInstanceOf[MappingException]
////        println(foo.getLocalizedMessage)
////        println(foo.getMessage)
////        foo.printStackTrace()
//      }
      complete(BadRequest, Extraction.decompose(Map("error" -> errorMsg)) )
  }

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(routes)
}
