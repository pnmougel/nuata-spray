package org.nuata.services.commonsmedia

import java.net.URLEncoder

import akka.actor.ActorRefFactory
import org.json4s.Extraction._
import org.json4s.jackson.JsonMethods._
import org.nuata.core.json.Json4sProtocol
import org.nuata.core.routing.RouteProvider
import org.nuata.services.commonsmedia.model.{CommonsMediaInfo, CommonsMediaMeta, MediaInfo}
import org.nuata.services.commonsmedia.queries.MediaInfoQuery
import spray.client.pipelining._
import spray.http.HttpHeaders.Accept
import spray.http.{HttpRequest, HttpResponse, MediaTypes}
import spray.httpx.encoding.{Deflate, Gzip}
import spray.routing._
import org.nuata.core.directives.GetParamsDirective._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by nico on 25/02/16.
 */
object CommonsMediaRoute extends RouteProvider with Json4sProtocol {

  def getMediaUrlFromSource(mediaName: String)(implicit refFactory: ActorRefFactory) : Future[Option[String]] = {
    val pipeline: HttpRequest => Future[CommonsMediaInfo] = (
      addHeader(Accept(MediaTypes.`application/json`))
        ~> encode(Gzip)
        ~> sendReceive
        ~> decode(Deflate)
        ~> unmarshal[CommonsMediaInfo])
    val encodedName = URLEncoder.encode(mediaName.name.replaceAllLiterally(" ", "_"), "UTF-8")
    val url = s"https://commons.wikimedia.org/w/api.php?action=query&format=json&list=allimages&aiprefix=${encodedName}"
    pipeline(Get(url)).map { commonsMediaInfo =>
      for(mediaInfo <- commonsMediaInfo.query.allimages.headOption) yield {
        mediaInfo.url
      }
    }
  }

  def getMediaMetaFromSource(mediaName: String)(implicit refFactory: ActorRefFactory) : Future[Option[CommonsMediaMeta]] = {
    val pipeline: HttpRequest => Future[HttpResponse] = (
      addHeader(Accept(MediaTypes.`application/json`))
        ~> encode(Gzip)
        ~> sendReceive
        ~> decode(Deflate))
    val encodedName = URLEncoder.encode(mediaName.name.replaceAllLiterally(" ", "_"), "UTF-8")
    val url = s"https://commons.wikimedia.org/w/api.php?action=query&titles=File:${encodedName}&prop=imageinfo&iiprop=extmetadata&format=json"
    pipeline(Get(url)).map { response =>
      val body = response.entity.asString
      val children = (parse(body) \ "query" \ "pages").children
      for(child <- children.headOption) yield {
        (child \ "imageinfo" \ "extmetadata").extract[CommonsMediaMeta]
      }
    }
  }

  def getMediaInfoFromSource(mediaName: String)(implicit refFactory: ActorRefFactory): Future[MediaInfo] = {
    val futureUrl = getMediaUrlFromSource(mediaName)
    val futureMeta = getMediaMetaFromSource(mediaName)

    val nameParts = mediaName.split('.')
    val extension = nameParts(nameParts.length - 1).toLowerCase

    for(url <- futureUrl; metaOpt <- futureMeta) yield {
      var mediaInfo = MediaInfo(None, None, mediaName, url, extension)

      for(meta <- metaOpt) {
        val date = meta.getStringFromMeta(meta.DateTime)
        val licenseShortName = meta.getStringFromMeta(meta.LicenseShortName)
        val restrictions = meta.getStringFromMeta(meta.Restrictions)
        val usageTerms = meta.getStringFromMeta(meta.UsageTerms)
        val licenseUrl = meta.getStringFromMeta(meta.LicenseUrl)
        val copyrighted = meta.getBooleanFromMeta(meta.Copyrighted)
        val attributionRequired = meta.getBooleanFromMeta(meta.AttributionRequired)
        val license = meta.getStringFromMeta(meta.License)
        val credit = meta.getStringFromMeta(meta.Credit)
        val description = meta.getStringFromMeta(meta.ImageDescription)

        mediaInfo = mediaInfo.copy(date = date, licenseShortName = licenseShortName, restrictions = restrictions,
          usageTerms = usageTerms, licenseUrl = licenseUrl, copyrighted = copyrighted, attributionRequired = attributionRequired,
          license = license, credit = credit, description = description)
      }
      mediaInfo
    }
  }

  def getMediaInfo(mediaName: String, update: Boolean)(implicit refFactory: ActorRefFactory): Future[MediaInfo] = {
    if(update) {
      getMediaInfoFromSource(mediaName).map { mediaInfo =>
        CommonsMediaRepository.setMediaInfo(mediaName, mediaInfo)
        mediaInfo
      }
    } else {
      CommonsMediaRepository.getMediaInfo(mediaName).flatMap { optMediaInfo =>
        optMediaInfo.map(m => Future(m)).getOrElse {
          getMediaInfoFromSource(mediaName).map { mediaInfo =>
            CommonsMediaRepository.setMediaInfo(mediaName, mediaInfo)
            mediaInfo
          }
        }
      }
    }
  }

  def route(implicit settings: RoutingSettings, refFactory: ActorRefFactory): Route = {
    (pathPrefix("commonsmedia") & getParams[MediaInfoQuery] & get) { mediaInfoQuery =>
      complete(getMediaInfo(mediaInfoQuery.name, mediaInfoQuery.update))
    }
  }
}
