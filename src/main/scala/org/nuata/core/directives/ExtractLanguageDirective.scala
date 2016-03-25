package org.nuata.core.directives

import spray.http.HttpHeaders.RawHeader
import spray.routing.{Directive1, HttpService}

/**
 * Created by nico on 20/02/16.
 */

trait ExtractLanguageDirective extends HttpService {
  def getLang: Directive1[String] = {
    optionalHeaderValueByName("language").flatMap {
      case Some(value) => provide(value)
      case None =>
        extract { ctx =>
          val lang = ctx.request.headers
            .filter(_.is("accept-language"))
            .map(_.value)
            .headOption
            .getOrElse("en")
          mapRequest(r => r.withHeaders(r.headers :+ RawHeader("language", lang))) & provide(lang)
          lang
        }
//        val lang = "en"
//        mapRequest(r => r.withHeaders(r.headers :+ RawHeader("language", lang))) & provide(lang)
    }
  }
}