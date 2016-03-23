package org.nuata.language

import akka.actor.ActorRefFactory
import org.nuata.core.json.Json4sProtocol
import org.nuata.core.utils.LanguageDetector
import spray.http.StatusCodes._
import spray.routing._
import scala.concurrent.ExecutionContext.Implicits.global

import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.json4s.Extraction._

import org.nuata.core.routing.RouteProvider
import org.nuata.models._

/**
 * Created by nico on 01/03/16.
 */
object LanguageRoutes extends RouteProvider with Json4sProtocol {
  val languages = Seq(
    Map("code" -> "en", "label" -> "English"),
    Map("code" -> "pt", "label" -> "Portuguese"),
    Map("code" -> "it", "label" -> "Italian"),
    Map("code" -> "ca", "label" -> "Catalan"),
    Map("code" -> "de", "label" -> "German"),
    Map("code" -> "es", "label" -> "Spanish"),
    Map("code" -> "pt-br", "label" -> "Brazilian"),
    Map("code" -> "fr", "label" -> "French")
  )

  def route(implicit settings: RoutingSettings, refFactory: ActorRefFactory): Route = {
    (path("languages") & get) {
      complete(languages)
    } ~ (path("language" / "detect" / Segment) & get) { text =>
      complete(LanguageDetector.getLanguage(text))
    }
  }
}
