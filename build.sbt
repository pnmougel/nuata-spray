organization  := "org.nuata"

version       := "0.1"

scalaVersion  := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",

    // Joda time
    "joda-time" % "joda-time" % "2.8.2",

    // Json4s
    "org.json4s" %% "json4s-jackson" % "3.3.0",
    "org.json4s" % "json4s-ext_2.10" % "3.3.0",

    // Elasticsearch
    "com.sksamuel.elastic4s" %% "elastic4s-core" % "1.7.4",
    "com.sksamuel.elastic4s" % "elastic4s-jackson_2.11" % "1.7.4"
  )
}

Revolver.settings
