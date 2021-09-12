package models

import play.api.libs.json.{Json, OFormat}

case class ProductBasket(id: Long, product: Long, stock: Long)

object ProductBasket {
  implicit val productBasketItemFormat: OFormat[BasketItem] = Json.format[BasketItem]
}

