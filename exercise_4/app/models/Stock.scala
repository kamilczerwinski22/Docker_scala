package models

import play.api.libs.json.{Json, OFormat}

case class Stock(id: Long, unitPrice: Int, totalPrice: Int, totalStock: Int)

object Stock {
  implicit val stockFormat: OFormat[Stock] = Json.format[Stock]
}