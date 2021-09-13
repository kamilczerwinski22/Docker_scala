package models

import play.api.libs.json._

case class User(id: Long, email: String,  password: String, nickname: String)

object User {
  implicit val userFormat: OFormat[User] = Json.format[User]
}
