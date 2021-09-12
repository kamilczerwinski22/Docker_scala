package models

import play.api.libs.json.{Json, OFormat}

case class ProductStock(id: Long, unitPrice: Int, totalPrice: Int, totalStock: Int)

object ProductStock {
  implicit val productStockFormat: OFormat[ProductStock] = Json.format[ProductStock]
}