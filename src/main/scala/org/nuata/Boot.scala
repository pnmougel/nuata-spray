package org.nuata

import java.io.{PrintWriter, File}
import java.lang.management.ManagementFactory

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import org.nuata.actors.HttpActor
import org.nuata.shared.{SinglePID, Settings}
import spray.can.Http

import scala.concurrent.duration._

object Boot extends App with SinglePID {
  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("nuata-system")

  // create and start our service actor
  val service = system.actorOf(Props[HttpActor], "http-service")

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = Settings.conf.getString("server.interface"), port = Settings.conf.getInt("server.port"))
}
