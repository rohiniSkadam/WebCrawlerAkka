package controllers

import java.net.URL

import akka.actor.{Actor, Status}
import org.jsoup.Jsoup

import scala.collection.JavaConverters._
import scala.util.{Failure, Success}

/**
  * Created by synerzip on 7/4/17.
  */

object TaskActor {
  case class Done() {}
  case class End() {}
}

class TaskActor(url: String, depth: Int) extends Actor {

  import TaskActor._

  implicit val ec = context.dispatcher

  val currentHost = new URL(url).getHost
  ClientActor.geturl(url) onComplete {
    case Success(body) => self ! body
    case Failure(err) => self ! Status.Failure(err)
  }

  def receive = {
    case body: String =>
      val links = Jsoup.parse(body, url).select("a[href]").iterator().asScala.map(_.absUrl("href"))
      links.foreach(context.parent ! LinkChecker.UrlCheck(_, depth))
    case _: Status.Failure => stop()
    case End => stop()
  }

  def stop(): Unit = {
    context.parent ! Done
    context.stop(self)
  }
}
