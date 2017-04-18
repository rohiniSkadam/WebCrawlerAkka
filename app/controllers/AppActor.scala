package controllers

import akka.actor.{Actor, ActorRef}
import controllers.ServerActor.Request
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.Await

/**
  * Created by synerzip on 7/4/17.
  */
class AppActor extends Actor {
  def receive = {
    case (serverActor: ActorRef, url: String, depth: Int)=>{
      implicit val timeout = Timeout(20 seconds)
      val future = serverActor ? Request(url, depth)
      val res=Await.result(future,timeout.duration)
      sender ! res
    }
  }
}
