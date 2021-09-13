package controllers.view

import models.{Coupon, Order, Payment, User}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.{CouponRepository, OrderRepository, PaymentRepository, UserRepository}
import javax.inject._

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class OrderController @Inject()(orderRepo: OrderRepository,
                                userRepo: UserRepository,
                                paymentRepo: PaymentRepository,
                                couponRepo: CouponRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  var userList: Seq[User] = Seq[User]()
  var paymentList: Seq[Payment] = Seq[Payment]()
  var couponList: Seq[Coupon] = Seq[Coupon]()

  fetchData()

  def listOrders: Action[AnyContent] = Action.async { implicit request =>
    orderRepo.list().map(orders => Ok(views.html.order_list(orders)))
  }

  def createOrder(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = Await.result(userRepo.list(), 1.second)
    val payments = Await.result(paymentRepo.list(), 1.second)
    val coupons = couponRepo.list()

    coupons.map(vouchers => Ok(views.html.order_create(orderForm, users, payments, vouchers)))
  }

  def createOrderHandle(): Action[AnyContent] = Action.async { implicit request =>
    orderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.order_create(errorForm, userList, paymentList, couponList))
        )
      },
      order => {
        orderRepo.create(order.userId, order.paymentId, order.couponId).map { _ =>
          Redirect("/form/order/list")
        }
      }
    )
  }

  def updateOrder(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val order = orderRepo.getByIdOption(id)
    order.map(order => {
      val prodForm = updateOrderForm.fill(UpdateOrderForm(order.get.id, order.get.userId, order.get.paymentId, order.get.couponId))
      Ok(views.html.order_update(prodForm, userList, paymentList, couponList))
    })
  }

  def updateOrderHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateOrderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.order_update(errorForm, userList, paymentList, couponList))
        )
      },
      order => {
        orderRepo.update(order.id, Order(order.id, order.userId, order.paymentId, order.couponId)).map { _ =>
          Redirect("/form/order/list")
        }
      }
    )
  }

  def deleteOrder(id: Long): Action[AnyContent] = Action {
    orderRepo.delete(id)
    Redirect("/form/order/list")
  }

  // utilities

  val orderForm: Form[CreateOrderForm] = Form {
    mapping(
      "userId" -> longNumber,
      "paymentId" -> longNumber,
      "couponId" -> longNumber,
    )(CreateOrderForm.apply)(CreateOrderForm.unapply)
  }

  val updateOrderForm: Form[UpdateOrderForm] = Form {
    mapping(
      "id" -> longNumber,
      "userId" -> longNumber,
      "paymentId" -> longNumber,
      "couponId" -> longNumber,
    )(UpdateOrderForm.apply)(UpdateOrderForm.unapply)
  }

  def fetchData(): Unit = {

    userRepo.list().onComplete {
      case Success(users) => userList = users
      case Failure(e) => print("error while listing users", e)
    }

    paymentRepo.list().onComplete {
      case Success(payment) => paymentList = payment
      case Failure(e) => print("error while listing payments", e)
    }

    couponRepo.list().onComplete {
      case Success(voucher) => couponList = voucher
      case Failure(e) => print("error while listing vouchers", e)
    }
  }
}

case class CreateOrderForm(userId: Long, paymentId: Long, couponId: Long)

case class UpdateOrderForm(id: Long, userId: Long, paymentId: Long, couponId: Long)