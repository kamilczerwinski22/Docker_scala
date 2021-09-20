package controllers.api

import models.Order
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import repositories.OrderRepository
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderController @Inject()(val orderRepository: OrderRepository,
                                cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def getOrderById(id: Long): Action[AnyContent] = Action.async {
    val order = orderRepository.getByIdOption(id)
    order.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("order not found")
    }
  }

  def listOrders(): Action[AnyContent] = Action.async {
    val orders = orderRepository.list()
    orders.map { orders =>
      Ok(Json.toJson(orders))
    }
  }

  def listOrdersByUserId(userId: Long): Action[AnyContent] = Action.async {
    val orders = orderRepository.listByUserId(userId)
    orders.map { orders =>
      Ok(Json.toJson(orders))
    }
  }

  def createOrder(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Order].map {
      order =>
        orderRepository.create(order.userId, order.addressId, order.paymentId, order.voucherId).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("incorrect data")))
  }

  def updateOrder(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Order].map {
      order =>
        orderRepository.update(order.id, order).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteOrder(id: Long): Action[AnyContent] = Action.async {
    orderRepository.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
