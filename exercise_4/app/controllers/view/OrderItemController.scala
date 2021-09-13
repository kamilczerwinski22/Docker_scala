package controllers.view

import models.{Order, OrderItem, Product}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.{OrderItemRepository, OrderRepository, ProductRepository}
import javax.inject._

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class OrderItemController @Inject()(orderItemRepository: OrderItemRepository,
                                    orderRepo: OrderRepository,
                                    productRepo: ProductRepository,
                                    cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  var orderList: Seq[Order] = Seq[Order]()
  var productList: Seq[Product] = Seq[Product]()
  fetchData()

  def listOrderItems: Action[AnyContent] = Action.async { implicit request =>
    orderItemRepository.list().map(orderProducts => Ok(views.html.order_item_list(orderProducts)))
  }

  def createOrderItem(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val orders = Await.result(orderRepo.list(), 1.second)
    val products = productRepo.list()

    products.map(orderProducts => Ok(views.html.order_item_create(orderProductForm, orders, orderProducts)))
  }

  def createOrderItemHandle(): Action[AnyContent] = Action.async { implicit request =>
    orderProductForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.order_item_create(errorForm, orderList, productList))
        )
      },
      orderItem => {
        orderItemRepository.create(orderItem.orderId, orderItem.itemId, orderItem.amount).map { _ =>
          Redirect("/form/order-item/list")
        }
      }
    )
  }

  def updateOrderItem(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val orderProduct = orderItemRepository.getByIdOption(id)
    orderProduct.map(orderItem => {
      val prodForm = updateOrderProductForm.fill(UpdateOrderItemForm(orderItem.get.id, orderItem.get.orderId, orderItem.get.itemId, orderItem.get.amount))
      Ok(views.html.order_item_update(prodForm, orderList, productList))
    })
  }

  def updateOrderItemHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateOrderProductForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.order_item_update(errorForm, orderList, productList))
        )
      },
      odrerItem => {
        orderItemRepository.update(odrerItem.id, OrderItem(odrerItem.id, odrerItem.orderId, odrerItem.itemId, odrerItem.amount)).map { _ =>
          Redirect("/form/order-item/list")
        }
      }
    )
  }

  def deleteOrderItem(id: Long): Action[AnyContent] = Action {
    orderItemRepository.delete(id)
    Redirect("/form/order-item/list")
  }

  val orderProductForm: Form[CreateOrderItemForm] = Form {
    mapping(
      "orderId" -> longNumber,
      "itemId" -> longNumber,
      "amount" -> number,
    )(CreateOrderItemForm.apply)(CreateOrderItemForm.unapply)
  }

  val updateOrderProductForm: Form[UpdateOrderItemForm] = Form {
    mapping(
      "id" -> longNumber,
      "orderId" -> longNumber,
      "itemId" -> longNumber,
      "amount" -> number,
    )(UpdateOrderItemForm.apply)(UpdateOrderItemForm.unapply)
  }

  def fetchData(): Unit = {

    orderRepo.list().onComplete {
      case Success(orders) => orderList = orders
      case Failure(e) => print("error while listing orders", e)
    }

    productRepo.list().onComplete {
      case Success(products) => productList = products
      case Failure(e) => print("error while listing products", e)
    }
  }
}

case class CreateOrderItemForm(orderId: Long, itemId: Long, amount: Int)

case class UpdateOrderItemForm(id: Long, orderId: Long, itemId: Long, amount: Int)