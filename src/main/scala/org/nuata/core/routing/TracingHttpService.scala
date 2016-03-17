package org.nuata.core.routing

//import kamon.Kamon
//import kamon.metric.instrument.Time
//import kamon.trace.Tracer
import spray.routing._

/**
 * Created by nico on 11/03/16.
 */
trait TracingHttpService extends HttpServiceBase {
  override def sealRoute(route: Route)(implicit eh: ExceptionHandler, rh: RejectionHandler): Route = {
    mapRequestContext { ctx: RequestContext =>
      val path = ctx.request.uri.path.toString()
      val method = ctx.request.method.name
      val start = System.nanoTime()
      val tagBuilder = Map.newBuilder[String, String]
      tagBuilder += "path" -> path
      tagBuilder += "method" -> method
      ctx.withHttpResponseMapped { response =>
        val duration = System.nanoTime() - start
        tagBuilder += "status-code" -> response.status.intValue.toString
//        Kamon.metrics.histogram(
//          s"query-response",
//          tagBuilder.result(),
//          Time.Nanoseconds)
//          .record(duration)
        response
      }
    } {
      (handleExceptions(eh) & handleRejections(sealRejectionHandler(rh)))(route)
    }
  }
}
