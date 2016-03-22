package org.nuata.core.routing

//import kamon.Kamon
//import kamon.metric.instrument.Time
//import kamon.trace.Tracer

import org.nuata.logging.requests.{LogRequestRepository, LogRequest}
import spray.routing._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

/**
 * Created by nico on 11/03/16.
 */
trait TracingHttpService extends HttpServiceBase {
  override def sealRoute(route: Route)(implicit eh: ExceptionHandler, rh: RejectionHandler): Route = {
    mapRequestContext { case ctx: RequestContext =>
      val path = ctx.request.uri.path.toString()
      val pathParts = path.split('/').filter(_.nonEmpty)
      val method = ctx.request.method.name
      val start = System.nanoTime()
      var ip: Option[String] = None
      val headers = ctx.request.headers.map { header =>
        if(header.is("remote-address")) {
          ip = Some(header.value)
        }
        header.toString()
      }
      val queryStr = ctx.request.uri.query.toString()

      val query: Map[String, List[String]] = queryStr.split("&").filter(_.nonEmpty).map { param =>
        val elems = param.split('=')
        if(elems.size == 1) {
          (elems(0), "")
        } else if (elems.size >= 2) {
          (elems(0), elems(1))
        } else {
          ("", "")
        }
      }.groupBy(_._1).map { case (key, values) =>
        (key, values.map(_._2).toList)
      }
      val body = if(ctx.request.message.entity.isEmpty) {
        None
      } else {
        Try(ctx.request.message.entity.asString).toOption
      }

      val logRequest = LogRequest(None, None, start, path, pathParts, query, method, body, headers, ip, None, None, None)
      val futureLogRequest = LogRequestRepository.indexAndMap(logRequest)

      ctx.withHttpResponseMapped { response =>
        val responseString = response.message.entity.asString
        val duration = System.nanoTime() - start
        futureLogRequest.map { logRequest =>
          LogRequestRepository.update(logRequest._id.get, prev => {
            prev.copy(status = Some(response.status.intValue), duration = Some(duration), response = Some(responseString))
          })
        }
        response
      }
    } {
      (handleExceptions(eh) & handleRejections(sealRejectionHandler(rh)))(route)
    }
  }
}
