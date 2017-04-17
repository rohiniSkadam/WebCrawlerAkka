package controllers
import akka.actor.{ActorSystem, Props}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import scala.collection.immutable.HashSet
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

/**
  * Created by synerzip on 7/4/17.
  */
object Application extends Controller with App{

  def webcrawl = Action.async { request =>
    val jsonRequest = request.body.asJson.get
    val url = jsonRequest.as[Client]
    implicit val timeout = Timeout(20 seconds)

    val system = ActorSystem("WebCrawler")
    val serverActor = system.actorOf(Props[ServerActor], "ServerActor")
    val appActor = system.actorOf(Props[AppActor], "appActor")

    val future=appActor ? (serverActor,url.url,2)
    val result=Await.result(future,timeout.duration).asInstanceOf[HashSet[String]]

     Future(Ok(Json.toJson(result)))
  }

}