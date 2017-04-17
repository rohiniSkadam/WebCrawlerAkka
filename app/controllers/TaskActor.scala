package controllers

import java.net.URL

import akka.actor.{Actor, Status}
import org.jsoup.Jsoup

import scala.collection.JavaConverters._
import scala.util.{Failure, Success}

object TaskActor {

  case class Done() {}

  case class Abort() {}

}

class TaskActor(url: String, depth: Int) extends Actor {

  import TaskActor._

  implicit val ec = context.dispatcher

  val currentHost = new URL(url).getHost
  ClientActor.get(url) onComplete {
    case Success(body) => self ! body
    case Failure(err) => self ! Status.Failure(err)
  }

  def receive = {
    case body: String =>
      val links = getAllLinks(body)
      links.foreach(context.parent ! LinkChecker.CheckUrl(_, depth))
    case _: Status.Failure => stop()
    case Abort => stop()
  }

  def getAllLinks(body: String): Iterator[String] = {
    Jsoup.parse(body, url).select("a[href]").iterator().asScala.map(_.absUrl("href"))
  }

  def stop(): Unit = {
    context.parent ! Done
    context.stop(self)
  }

}
