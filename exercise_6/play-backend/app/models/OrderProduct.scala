package models
import play.api.libs.json._

case class OrderProduct(id: Long = 0, orderId: Long, productId: Long, amount: Int)

object OrderProduct{
  implicit val orderProductFormat: OFormat[OrderProduct] = Json.using[Json.WithDefaultValues].format[OrderProduct]
}
