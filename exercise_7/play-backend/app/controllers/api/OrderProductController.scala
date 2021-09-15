package controllers.api

import models.OrderProduct
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.OrderProductRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderProductController @Inject()(val orderProductRepo: OrderProductRepository,
                                       cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def getOrderProductById(id: Long): Action[AnyContent] = Action.async {
    val orderProduct = orderProductRepo.getByIdOption(id)
    orderProduct.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("orderProduct with given id cannot be found")
    }
  }

  def listOrderProducts(): Action[AnyContent] = Action.async {
    val orderProducts = orderProductRepo.list()
    orderProducts.map { orderProducts =>
      Ok(Json.toJson(orderProducts))
    }
  }

  def listProductsByOrderId(orderId: Long): Action[AnyContent] = Action.async {
    val orderProducts = orderProductRepo.listProductsByOrderId(orderId)
    orderProducts.map { orderProducts =>
      Ok(Json.toJson(orderProducts))
    }
  }

  def listOrdersByProductId(productId: Long): Action[AnyContent] = Action.async {
    val orderProducts = orderProductRepo.listOrdersByProductId(productId)
    orderProducts.map { orderProducts =>
      Ok(Json.toJson(orderProducts))
    }
  }

  def createOrderProduct(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[OrderProduct].map {
      orderProduct =>
        orderProductRepo.create(orderProduct.orderId, orderProduct.productId, orderProduct.amount).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("incorrect data have been provided")))
  }

  def updateOrderProduct(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[OrderProduct].map {
      orderProduct =>
        orderProductRepo.update(orderProduct.id, orderProduct).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteOrderProduct(id: Long): Action[AnyContent] = Action.async {
    orderProductRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
