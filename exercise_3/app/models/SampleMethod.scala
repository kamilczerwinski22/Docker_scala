package models

import play.api.libs.json._

case class SampleMethod(id: String, name: String)

object SampleMethod {
  implicit val format: OFormat[SampleMethod] = Json.format[SampleMethod]
}
