package sbt.tasks

import java.io.PrintWriter
import org.nuata.core.routing.RouteProvider
import org.nuata.core.utils.reflections.Reflection

import scala.io.Source


/**
 * Created by nico on 23/03/16.
 */
object UpdateRoutes {
  def main(args: Array[String]) {
    val stream = getClass.getResourceAsStream("/templates/RouteService.template")
    val template = Source.fromInputStream( stream ).getLines.mkString("\n")

    val instances = Reflection.getInstancesOf[RouteProvider]
    val routeNames = instances.map { route =>
      route.getClass.getCanonicalName.replaceAllLiterally("$", ".route")
    }
    println(s"Loaded ${routeNames.size} routes")

    val routes = routeNames.mkString("List(", ", \n\t", ")")
    val outTemplate = template.replaceAllLiterally("[[routes]]", routes)

    val pw = new PrintWriter("src/main/scala/org/nuata/core/routing/RouteService.scala")
    pw.println(outTemplate)
    pw.flush()
    pw.close()
  }
}
