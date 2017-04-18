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

/**
  * Parse the URL & returns the links
  * @param url - input url
  * @param depth - depth of crawling
  */
class TaskActor(url: String, depth: Int) extends Actor {

  import TaskActor._

  implicit val ec = context.dispatcher

  val currentHost = new URL(url).getHost
  CrawlClient.geturl(url) onComplete {
    case Success(body) => self ! body
    case Failure(err) => self ! Status.Failure(err)
  }

  def receive = {
    case body: String =>
      val links = Jsoup.parse(body, url).select("a[href]").iterator().asScala.map(_.absUrl("href"))
      links.foreach(url=>{
        context.parent ! LinkChecker.UrlCheck(url, depth)
      })
    case _: Status.Failure => stop()
    case End => stop()
  }

  /**
    * Sends the Done message to parent Actor & Stops the current Actor
    */
  def stop(): Unit = {
    context.parent ! Done
    context.stop(self)
  }
}
