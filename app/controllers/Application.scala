package controllers
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import controllers.ServerActor._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.collection.immutable.HashSet
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Application extends Controller {
  var linkset: HashSet[String] = _

  def webcrawl = Action.async { request =>
    val jsonRequest = request.body.asJson.get
    val url = jsonRequest.as[Client]

    val system = ActorSystem("WebCrawler")
    val serverActor = system.actorOf(Props[ServerActor], "ServerActor")
    val appActor = system.actorOf(Props(new AppActor(serverActor, url.url, 2)), "appActor")

     Future(Ok(Json.toJson("HI")))

  }

  class AppActor(serverActor: ActorRef, url: String, depth: Int) extends Actor {

    serverActor ! webCrawlerRequest(url, depth)
    def receive = {
      case webCrawlerResponse(root, links) => {
        links.foreach(l => {
          println(l)
        })
        print("Done")
        linkset = links.asInstanceOf[HashSet[String]]
      }
    }
  }
}