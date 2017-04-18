package controllers
import akka.actor.{ActorSystem, Props}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import akka.pattern.ask
import akka.util.Timeout
import controllers.ServerActor.Request

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

/**
  * Created by synerzip on 7/4/17.
  */

/**
  * Creates Actor system
  *
  */
object Application extends Controller {

  def webcrawl = Action.async { request =>
    val jsonRequest = request.body.asJson.get
    val url = jsonRequest.as[Client]
    implicit val timeout = Timeout(20 seconds)

    val system = ActorSystem("WebCrawler")
    val serverActor = system.actorOf(Props[ServerActor], "ServerActor")

    val future = serverActor ? Request(url.url, 2)
    val res=Await.result(future,timeout.duration).asInstanceOf[Map[String,Int]]

     Future(Ok(Json.toJson(res)))
  }

}