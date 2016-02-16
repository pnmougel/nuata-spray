//import deployssh.DeploySSH.{ArtifactSSH, ServerConfig}

import sbtassembly.AssemblyKeys.assemblyOutputPath
import sbtassembly.{PathList, MergeStrategy}
import spray.revolver.RevolverPlugin.Revolver

organization  := "org.nuata"

version       := "0.1"

scalaVersion  := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

// Fix bug in assembly task for elastic4s with joda convert
//assemblyExcludedJars in assembly := {
//  val cp = (fullClasspath in assembly).value
//  cp filter { foo => foo.data.getName == "joda-time-2.8.2.jar"}
//}

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
 {
 case PathList("org", "joda", "time", "base", "BaseDateTime.class")     => MergeStrategy.first
//  case "BaseDateTime.class"     => MergeStrategy.first
  case x =>
    //val old = (mergeStrategy in assembly).value
       old(x)
   }
}

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  val elasticSearchV = "2.2.0"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "io.spray"            %%  "spray-client"  % "1.3.1",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",

    // Slf4j
    "org.slf4j"    % "slf4j-simple"    % "1.7.1",
    "org.slf4j"    % "slf4j-api"    % "1.7.1",
    "org.slf4j"    % "log4j-over-slf4j"  % "1.7.1",

    // Elasticsearch
    ("com.sksamuel.elastic4s" %% "elastic4s-core" % elasticSearchV % "provided").exclude("org.joda.time.base", "base"),
    "com.sksamuel.elastic4s" % "elastic4s-jackson_2.11" % elasticSearchV,


    // Json4s
    "org.json4s" %% "json4s-jackson" % "3.3.0",
    "org.json4s" % "json4s-ext_2.10" % "3.3.0",

    // Language detection
    "com.optimaize.languagedetector" % "language-detector" % "0.5",

    // Bcrypt
    "com.github.t3hnar" % "scala-bcrypt_2.10" % "2.5",

    // Probably not required, supposed to fix a bug in elastic4s script update
//    "org.codehaus.groovy" % "groovy-all" % "2.4.1",

    // Http request
    "org.scalaj" %% "scalaj-http" % "2.2.0",

    // Reflections
    "org.reflections" % "reflections" % "0.9.10",

    // Joda time
    "joda-time" % "joda-time" % "2.8.2"
  )
}


lazy val root = (project in file("."))
  .settings(
    name := "nuata-api"
  )

lazy val deploy = taskKey[Int]("Deploy the artifacts to the server")

deploy := {
  val log = streams.value.log
  val assemblyFile = (assemblyOutputPath in assembly).value
  val account = "nico@nuata.org"
  val src = assemblyFile.getAbsolutePath
  val target = "~/api/" + assemblyFile.getName
  val targetAbs = account + ":" + target
  val keyFile = System.getProperty("user.home") + "/.ssh/id_rsa.pub"

  log.info("Removing previous file")
  s"ssh -i ${keyFile} ${account} rm -f ${target}".!!

  log.info("Copying " + src + " to " + targetAbs)
  Process(List("scp", "-i", keyFile, src, targetAbs)).run()

  val fileSize = assemblyFile.length
  var curSize = 0
  val sizeCmd = s"du -b ${target} | cut -f1"
  while(curSize != fileSize) {
    Thread.sleep(500)
    val curSizeStr = s"ssh -i ${keyFile} ${account} ${sizeCmd}".!!
    if(!curSizeStr.isEmpty) {
      curSize = curSizeStr.trim.replaceAllLiterally("\"", "").toInt
    }
    log.info(f"${curSize.toDouble / (1024 * 1024)}%.2f Mo / ${fileSize.toDouble / (1024 * 1024) }%.2f Mo\t(${100 * curSize.toDouble / fileSize}%.2f" + " %)")
  }

  val symlink = "api/nuata-latest.jar"
  log.info("Symlink to  " + symlink)
  val cmd = "ln -sf " + target + " " + symlink
  s"ssh -i ${keyFile} ${account} ${cmd}".!!

  log.info("Starting the server")
  val cmdRun = "nohup java -jar ~/api/nuata-latest.jar 2>> ~/api/errors.log 1>> ~/api/api.log"
  Process(List("ssh", "-i", keyFile, account, cmdRun)).run()
  0
}

(deploy) <<= (deploy) dependsOn (assembly)



Revolver.settings
