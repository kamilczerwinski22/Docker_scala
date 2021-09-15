package models

import play.api.libs.json._

case class Stock(id: Long = 0, unitPrice: Int, totalPrice: Int, totalStock: Int)

object Stock {
  implicit val stockFormat: OFormat[Stock] = Json.using[Json.WithDefaultValues].format[Stock]
}
