package models

import play.api.libs.json.{Json, OFormat}

case class Order(id: Long, userId: Long, paymentId: Long, couponId: Long)

object Order {
  implicit val basketFormat: OFormat[Order] = Json.format[Order]
}
