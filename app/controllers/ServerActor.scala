package controllers

import akka.actor.{Actor, ActorRef, Props}
import controllers.LinkChecker.Result
import controllers.ServerActor.webCrawlerRequest
import scala.collection.mutable

/**
  * Created by synerzip on 7/4/17.
  */
object ServerActor {

  case class webCrawlerRequest(url: String, depth: Integer) {}
}

class ServerActor extends Actor {
  val clientMap: mutable.Map[String, Set[ActorRef]] = mutable.Map[String, Set[ActorRef]]()
  val urlMap: mutable.Map[String, ActorRef] = mutable.Map[String, ActorRef]()

  def receive = {
    case webCrawlerRequest(url, depth) =>
      val controller = urlMap.get(url)
      if (controller.isEmpty) {
        urlMap += (url -> context.actorOf(Props(new LinkChecker(url, depth))))
        clientMap += (url -> Set.empty[ActorRef])
      }
      clientMap(url) += sender
    case Result(url, links) =>
      clientMap(url).foreach(l=>{
        l ! links
      })
  }
}
