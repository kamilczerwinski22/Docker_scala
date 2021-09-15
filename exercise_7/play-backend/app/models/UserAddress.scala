package models

import play.api.libs.json._

case class UserAddress(id: Long = 0, userId: Long, firstname: String, lastname: String, address: String,
                       zipcode: String, city: String, country: String)

object UserAddress {
  implicit val userAddressFormat: OFormat[UserAddress] = Json.using[Json.WithDefaultValues].format[UserAddress]
}
