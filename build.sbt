import play.Project._

name := "hello-play-scala"

version := "1.0"

scalaVersion := "2.11.8"


libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.2",
  "org.webjars" % "bootstrap" % "2.3.1"
)

libraryDependencies += "org.jsoup" % "jsoup" % "1.8.3"

libraryDependencies += "org.asynchttpclient" % "async-http-client" % "2.1.0-alpha11"

playScalaSettings