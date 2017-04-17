package controllers

import akka.actor.{Actor, ActorRef, Props, ReceiveTimeout}
import controllers.LinkChecker.{CheckUrl, Result}
import controllers.TaskActor.Done

import scala.concurrent.duration._

/**
  * Created by synerzip on 7/4/17.
  */
object LinkChecker {
  case class CheckUrl(url: String, depth: Int) {}
  case class Result(url: String, links: Set[String]) {}
}

class LinkChecker(root: String, newDepth: Integer) extends Actor {

  var temp = Set.empty[String]
  var childActor = Set.empty[ActorRef]

  self ! CheckUrl(root, newDepth)
  context.setReceiveTimeout(10 seconds)

  def receive = {
    case CheckUrl(url, depth) =>
      if (!temp(url) && depth > 0)
        childActor += context.actorOf(Props(new TaskActor(url, depth - 1)))
      temp += url
    case Done =>
      childActor -= sender
      if (childActor.isEmpty) context.parent ! Result(root, temp)
    case ReceiveTimeout => childActor.foreach(timeout=>{
      timeout ! TaskActor.Abort
    })
  }
}
