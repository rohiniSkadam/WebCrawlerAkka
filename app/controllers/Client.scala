package controllers

import play.api.libs.json.Json

/**
  * Created by synerzip on 17/4/17.
  */
case class Client(url: String)

object Client {
  implicit val urlFormat = Json.format[Client]
}