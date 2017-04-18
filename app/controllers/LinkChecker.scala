package controllers

import akka.actor.{Actor, ActorRef, Props, ReceiveTimeout}
import controllers.LinkChecker.{UrlCheck, Result}
import controllers.TaskActor.Done

import scala.concurrent.duration._

/**
  * Created by synerzip on 7/4/17.
  */
object LinkChecker {
  case class UrlCheck(url: String, depth: Int) {}
  case class Result(url: String, links: Set[String]) {}
}

class LinkChecker(root: String, newDepth: Integer) extends Actor {

  var temp = Set.empty[String]
  var child = Set.empty[ActorRef]

  self ! UrlCheck(root, newDepth)
  context.setReceiveTimeout(10 seconds)

  def receive = {
    case UrlCheck(url, depth) =>
      if (!temp(url) && depth > 0)
        child += context.actorOf(Props(new TaskActor(url, depth - 1)))
      temp += url
    case Done =>
      child -= sender
      if (child.isEmpty) context.parent ! Result(root, temp)
    case ReceiveTimeout => child.foreach(reciveTimeout=>{
      reciveTimeout ! TaskActor.End
    })
  }
}
