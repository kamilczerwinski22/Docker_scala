package models

import play.api.libs.json._


case class CreditCard(id: Long = 0, userId: Long, cardholderName: String, number: String, expDate: String,
                      cvcCode: String)

object CreditCard {
  implicit val creditCardFormat: OFormat[CreditCard] = Json.using[Json.WithDefaultValues].format[CreditCard]
}
