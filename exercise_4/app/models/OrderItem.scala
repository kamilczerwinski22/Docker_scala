package models

import play.api.libs.json.{Json, OFormat}

case class OrderItem(id: Long, orderId: Long, itemId: Long, amount: Int)

object OrderItem{
  implicit val orderItemFormat: OFormat[OrderItem] = Json.format[OrderItem];
}