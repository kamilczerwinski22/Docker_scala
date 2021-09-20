package models

import play.api.libs.json.{Json, OFormat}

case class SignUpRequest(email: String, password: String)

object SignUpRequest {
  implicit val signUpRequestForm: OFormat[SignUpRequest] = Json.using[Json.WithDefaultValues].format[SignUpRequest]
}
