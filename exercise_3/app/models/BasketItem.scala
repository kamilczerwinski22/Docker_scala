package models

import play.api.libs.json.{Json, OFormat}

case class BasketItem(id: Long, basketId: Long, productId: Long, amount: Int)

object BasketItem {
  implicit val basketItemFormat: OFormat[BasketItem] = Json.format[BasketItem]
}
