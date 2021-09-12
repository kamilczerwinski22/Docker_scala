package models

import play.api.libs.json.{Json, OFormat}

case class Payment(id: Long, userId: Long, chargeAmount: Int)

object Payment {
  implicit val paymentFormat: OFormat[Payment] = Json.format[Payment]
}
