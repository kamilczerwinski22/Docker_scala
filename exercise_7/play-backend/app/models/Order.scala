package models


import play.api.libs.json._

case class Order(id: Long = 0, userId: Long, addressId: Long, paymentId: Long, voucherId: Long = 0)

object Order {
  implicit val orderFormat: OFormat[Order] = Json.using[Json.WithDefaultValues].format[Order]
}
