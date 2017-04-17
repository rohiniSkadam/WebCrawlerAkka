package controllers

import akka.actor.{Actor, ActorRef, Props}
import controllers.LinkChecker.Result
import controllers.ServerActor.{webCrawlerRequest, webCrawlerResponse}
import scala.collection.mutable

object ServerActor {

  case class webCrawlerRequest(url: String, depth: Integer) {}

  case class webCrawlerResponse(url: String, links: Set[String]) {}

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
        l ! webCrawlerResponse(url,links)
      })
  }
}
