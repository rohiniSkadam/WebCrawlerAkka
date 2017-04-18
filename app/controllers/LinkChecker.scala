package controllers

import akka.actor.{Actor, ActorRef, Props, ReceiveTimeout}
import controllers.LinkChecker.{Result, UrlCheck}
import controllers.TaskActor.Done

import scala.concurrent.duration._

/**
  * Created by synerzip on 7/4/17.
  */
object LinkChecker {
  case class UrlCheck(url: String, depth: Int) {}
  case class Result(url: String, links: Map[String,Int]) {}
}

/**
  * Check URL & sends the result
  * @param root - input url
  * @param depth - depth of crawling
  */
class LinkChecker(root: String, depth: Integer) extends Actor {
  var temp = Set.empty[String]
  var child = Set.empty[ActorRef]
  var temp2 =Map.empty[String,Int]
  val maxdepth=depth
  self ! UrlCheck(root, 0)
  context.setReceiveTimeout(10 seconds)

  def receive = {
    case UrlCheck(url, depth) =>
      if (!temp(url) && depth < maxdepth)
        child += context.actorOf(Props(new TaskActor(url, depth + 1)))
      temp += url
      temp2+=url->depth
    case Done =>
      child -= sender
      if (child.isEmpty) context.parent ! Result(root, temp2)
    case ReceiveTimeout => child.foreach(timeout=>{
      timeout ! TaskActor.End
    })
  }
}
