package controllers

import akka.actor.{Actor, ActorRef, Props}
import controllers.LinkChecker.Result
import controllers.ServerActor.Request
import scala.collection.mutable

/**
  * Created by synerzip on 7/4/17.
  */
object ServerActor {
  case class Request(url: String, depth: Integer) {}
}

/**
  * Sends url & depth to Linkchecker & maps the result with sender
  */
class ServerActor extends Actor {
  val clientMap: mutable.HashMap[String, Set[ActorRef]] = mutable.HashMap[String, Set[ActorRef]]()
  val urlMap: mutable.Map[String, ActorRef] = mutable.Map[String, ActorRef]()

  def receive = {
    case Request(url, depth) =>
      if (urlMap.get(url).isEmpty) {
        urlMap += (url -> context.actorOf(Props(new LinkChecker(url, depth))))
        clientMap += (url -> Set.empty[ActorRef])
      }
      clientMap(url) += sender
    case Result(url, links) =>
      clientMap(url).foreach(requestActor=>{
        requestActor ! links
      })
  }
}
