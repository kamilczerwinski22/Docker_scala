package models

import play.api.libs.json._

case class UserInfo(id: Long, userId: Long, firstname: String, lastname: String, address: String,
                    zipcode: String, city: String, country: String)

object UserInfo{
  implicit val userAddressFormat: OFormat[UserInfo] = Json.format[UserInfo]
}
