import play.Project._

name := "hello-play-scala"

version := "1.0-SNAPSHOT"


libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.2",
  "org.webjars" % "bootstrap" % "2.3.1"
)

libraryDependencies += "org.jsoup" % "jsoup" % "1.8.3"

libraryDependencies += "com.typesafe" % "config" % "1.3.1"

libraryDependencies += "org.asynchttpclient" % "async-http-client" % "2.1.0-alpha11"

playScalaSettings


