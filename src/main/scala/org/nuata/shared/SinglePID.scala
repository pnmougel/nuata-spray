package org.nuata.shared

import java.io.{PrintWriter, File}
import java.lang.management.ManagementFactory

import scala.io.Source
import scala.sys.process._

/**
 * Created by nico on 12/01/16.
 */
trait SinglePID {
  val apiFolder = new File(System.getProperty("user.home") + "/api")

  if(apiFolder.exists()) {
    // Get the running pid
    val runningPidFile = new File(System.getProperty("user.home") + "/api/RUNNING_PID")
    if (runningPidFile.exists()) {
      val pid = Source.fromFile(runningPidFile).getLines().mkString("\n")
      if (!pid.trim.isEmpty) {
        try {
          s"kill -9 ${pid}".!
        } catch {
          case e: RuntimeException => {
            println("Unable to kill pid " + pid)
          }
        }
        Thread.sleep(400)
      }
    }

    val curPid = ManagementFactory.getRuntimeMXBean().getName().split('@')(0)
    val pw = new PrintWriter(runningPidFile)
    pw.println(curPid)
    pw.flush()
    pw.close()
  }

}
