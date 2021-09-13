package models

import play.api.libs.json.{Json, OFormat}

case class Product(id: Long, stockId: Long, categoryId: Long, subcategoryId: Long, name: String, description: String)

object Product {
  implicit val productFormat: OFormat[Product] = Json.format[Product]
}



