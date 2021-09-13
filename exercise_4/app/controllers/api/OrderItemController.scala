package controllers.api

import akka.actor.ActorSystem
import javax.inject.Inject
import models.OrderItem
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.OrderItemRepository

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}

class OrderItemController @Inject()(val orderItemRepo: OrderItemRepository,
                                    cc: ControllerComponents, actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getOrderItemById(id: Long): Action[AnyContent] = Action.async {
    val orderProduct = orderItemRepo.getByIdOption(id)
    orderProduct.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("orderProduct cannot be found")
    }
  }

  def listOrderItems(): Action[AnyContent] = Action.async {
    val orderProducts = orderItemRepo.list()
    orderProducts.map { orderProducts =>
      Ok(Json.toJson(orderProducts))
    }
  }

  def listItemsByOrderId(orderId: Long): Action[AnyContent] = Action.async {
    val orderProducts = orderItemRepo.listProductsByOrderId(orderId)
    orderProducts.map { orderProducts =>
      Ok(Json.toJson(orderProducts))
    }
  }

  def listOrdersByItemId(productId: Long): Action[AnyContent] = Action.async {
    val orderProducts = orderItemRepo.listOrdersByProductId(productId)
    orderProducts.map { orderProducts =>
      Ok(Json.toJson(orderProducts))
    }
  }

  def createOrderItem(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[OrderItem].map {
      orderProduct =>
        orderItemRepo.create(orderProduct.orderId, orderProduct.itemId, orderProduct.amount).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("incorrect data provided")))
  }

  def updateOrderItem(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[OrderItem].map {
      orderProduct =>
        orderItemRepo.update(orderProduct.id, orderProduct).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteOrderItem(id: Long): Action[AnyContent] = Action.async {
    orderItemRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
