package controllers.api

import akka.actor.ActorSystem
import javax.inject.Inject
import models.{Order}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.OrderRepository

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration._

class OrderController @Inject()(val orderRepo: OrderRepository,
                                cc: ControllerComponents, actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  def getOrderById(id: Long): Action[AnyContent] = Action.async {
    val order = orderRepo.getByIdOption(id)
    order.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("order with given id cannot be found")
    }
  }

  def listOrders(): Action[AnyContent] = Action.async {
    val orders = orderRepo.list()
    orders.map { orders =>
      Ok(Json.toJson(orders))
    }
  }

  def listOrdersByUserId(userId: Long): Action[AnyContent] = Action.async {
    val orders = orderRepo.listByUserId(userId)
    orders.map { orders =>
      Ok(Json.toJson(orders))
    }
  }

  def listOrdersByPaymentId(paymentId: Long): Action[AnyContent] = Action.async {
    val orders = orderRepo.listByPaymentId(paymentId)
    orders.map { orders =>
      Ok(Json.toJson(orders))
    }
  }

  def listOrdersByCouponId(couponId: Long): Action[AnyContent] = Action.async {
    val orders = orderRepo.listByVoucherId(couponId)
    orders.map { orders =>
      Ok(Json.toJson(orders))
    }
  }

  def createOrder(): Action[JsValue] = Action.async(parse.json) { implicit request =>

    request.body.validate[Order].map {
      order =>
        orderRepo.create(order.userId, order.paymentId, order.couponId).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("incorrect data have been provided")))
  }

  def updateOrder(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Order].map {
      order =>
        orderRepo.update(order.id, order).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteOrder(id: Long): Action[AnyContent] = Action.async {
    orderRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

}
