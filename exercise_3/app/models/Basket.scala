package models

import play.api.libs.json.{Json, OFormat}

case class Basket(id: Long, userId: Long, paymentId: Long, couponId: Long)

object Basket {
  implicit val basketFormat: OFormat[Basket] = Json.format[Basket]
}
