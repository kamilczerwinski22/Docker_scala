package models

import play.api.libs.json.{Json, OFormat}

case class ProductDescription(id: Long, description: String)

object ProductDescription {
  implicit val productDescriptionFormat: OFormat[ProductDescription] = Json.format[ProductDescription]
}