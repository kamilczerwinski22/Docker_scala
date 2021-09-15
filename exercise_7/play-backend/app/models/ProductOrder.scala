package models

import models.util.TimestampFormatter
import play.api.libs.json._

import java.sql.Timestamp
import java.time.Instant

case class ProductOrder(id: Long = 0, product: Long, stock: Long, createdAt: Timestamp = Timestamp.from(Instant.now()), updatedAt: Timestamp = Timestamp.from(Instant.now()))

object ProductOrder extends TimestampFormatter {
  implicit val productOrderFormat: OFormat[ProductOrder] = Json.using[Json.WithDefaultValues].format[ProductOrder]
}
