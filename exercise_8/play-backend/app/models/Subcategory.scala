package models
import play.api.libs.json._

case class Subcategory(id: Long = 0, categoryId: Long, name: String)

object Subcategory{
  implicit val subcategoryFormat: OFormat[Subcategory] = Json.using[Json.WithDefaultValues].format[Subcategory]
}
