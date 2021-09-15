package controllers.api

import models.Order
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.OrderRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderController @Inject()(val orderRepository: OrderRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def getOrderById(id: Long): Action[AnyContent] = Action.async {

    val order = orderRepository.getByIdOption(id)
    order.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("order with given id cannot be found")
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

  def listOrdersByAddressId(addressId: Long): Action[AnyContent] = Action.async {

    val orders = orderRepository.listByAddressId(addressId)
    orders.map { orders =>
      Ok(Json.toJson(orders))
    }
  }

  def listOrdersByPaymentId(paymentId: Long): Action[AnyContent] = Action.async {
    val orders = orderRepository.listByPaymentId(paymentId)
    orders.map { orders =>
      Ok(Json.toJson(orders))
    }
  }

  def listOrdersByVoucherId(voucherId: Long): Action[AnyContent] = Action.async {
    val orders = orderRepository.listByVoucherId(voucherId)
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
    }.getOrElse(Future.successful(BadRequest("incorrect data have been provided")))
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
