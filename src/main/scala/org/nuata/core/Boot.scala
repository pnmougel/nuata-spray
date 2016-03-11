package org.nuata.core

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import akka.util.Timeout
import kamon.Kamon
import org.nuata.core.actors.HttpActor
import org.nuata.shared.{Settings, SinglePID}
import spray.can.Http
import scala.concurrent.duration._
import akka.pattern.ask
//import io.k

object Boot extends App with SinglePID {
  Kamon.start()


  val someCounter = Kamon.metrics.counter("some-counter")
  someCounter.increment()


  // we need an ActorSystem to host our application
  implicit val system = ActorSystem("nuata-system")

  // create and start our service actor
  val service = system.actorOf(Props[HttpActor], "http-service")

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = Settings.getString("server.interface"), port = Settings.getInt("server.port"))
}
