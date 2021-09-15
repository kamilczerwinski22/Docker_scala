package models


import play.api.libs.json._

case class Product(id: Long = 0, stockId: Long, categoryId: Long, subcategoryId: Long, name: String, imageUrl: String, description: String)

object Product{
  implicit val productFormat: OFormat[Product] = Json.using[Json.WithDefaultValues].format[Product]
}
