package models


import play.api.libs.json._


case class Payment(id: Long = 0, userId: Long, creditCardId: Long, amount: Int)

object Payment {
  implicit val paymentFormat: OFormat[Payment] = Json.using[Json.WithDefaultValues].format[Payment]
}
