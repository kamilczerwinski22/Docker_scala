package models

import play.api.libs.json._

case class PaymentMoney(id: Long, userId: Long, cardholderName: String)

object PaymentMoney {
  implicit val paymentMethodFormat: OFormat[PaymentMoney] = Json.format[PaymentMoney]
}
