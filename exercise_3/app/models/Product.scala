package models

import play.api.libs.json.{Json, OFormat}

case class Product(id: Long, stockId: Long, categoryId: Long, subcategoryId: Long, descriptionId: Long, name: String)

object Product {
  implicit val productFormat: OFormat[Product] = Json.format[Product]
}



