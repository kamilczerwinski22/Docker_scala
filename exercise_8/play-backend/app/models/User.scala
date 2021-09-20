package models

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import play.api.libs.json.{Json, OFormat}

case class User(id: Long, loginInfo: LoginInfo, email: String) extends Identity

object User {
  implicit val loginInfoFormat: OFormat[LoginInfo] = Json.using[Json.WithDefaultValues].format[LoginInfo]
  implicit val userFormat: OFormat[User] = Json.using[Json.WithDefaultValues].format[User]
}