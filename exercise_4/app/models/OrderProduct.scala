package models

import models.util.TimestampFormatter
import play.api.libs.json._

import java.sql.Timestamp
import java.time.Instant

case class OrderProduct(id: Long, orderId: Long, productId: Long, amount: Int)

object OrderProduct {
  implicit val orderProductFormat: OFormat[OrderProduct] = Json.format[OrderProduct]
}
