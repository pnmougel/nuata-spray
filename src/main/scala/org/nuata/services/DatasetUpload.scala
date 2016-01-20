package org.nuata.services

import org.json4s.Extraction
import org.nuata.models._
import org.nuata.repositories._
import org.nuata.services.routing.RouteRegistration
import org.nuata.shared.Json4sProtocol
import spray.http.MultipartFormData
import spray.http.StatusCodes._
import spray.routing._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by nico on 19/01/16.
 */
trait DatasetUpload extends RouteRegistration {

  def createDataset(content: Array[Byte]) = {

  }

  registerRoute {
    (pathPrefix("dataset") & post & entity(as[MultipartFormData])) { rq =>
      for(f <- rq.fields) {
        println(f.disposition)

        println(f.contentRange)
        println(f.filename)
        if(f.entity.data.hasFileBytes) {
          createDataset(f.entity.data.toByteArray)
        }
    //      f.entity.data.
    //      println(f.entity.data.asString)
      }
      complete("ok")
    }
  }
}
