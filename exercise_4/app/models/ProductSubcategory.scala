package models

import play.api.libs.json.{Json, OFormat}

case class ProductSubcategory(id: Long, categoryId: Long, name: String)

object ProductSubcategory {
  implicit val productSubcategoryFormat: OFormat[ProductSubcategory] = Json.format[ProductSubcategory]
}
