package models

import play.api.libs.json.{Json, OFormat}

case class Coupon(id: Long, amount: Int, usages: Int)

object Coupon {
  implicit val couponFormat: OFormat[Coupon] = Json.format[Coupon]
}
