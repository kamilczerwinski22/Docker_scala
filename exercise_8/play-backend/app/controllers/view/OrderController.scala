package controllers.view

import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import repositories._

import javax.inject._
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class OrderController @Inject()(orderRepo: OrderRepository,
                                userRepo: UserRepository,
                                addressRepository: UserAddressRepository,
                                paymentRepository: PaymentRepository,
                                voucherRepository: VoucherRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val url = "/form/order/list"

  val orderForm: Form[CreateOrderForm] = Form {
    mapping(
      "userId" -> longNumber,
      "addressId" -> longNumber,
      "paymentId" -> longNumber,
      "voucherId" -> longNumber,
    )(CreateOrderForm.apply)(CreateOrderForm.unapply)
  }

  val updateOrderForm: Form[UpdateOrderForm] = Form {
    mapping(
      "id" -> longNumber,
      "userId" -> longNumber,
      "addressId" -> longNumber,
      "paymentId" -> longNumber,
      "voucherId" -> longNumber,
    )(UpdateOrderForm.apply)(UpdateOrderForm.unapply)
  }

  def fetchData(): Unit = {
    userRepo.list().onComplete {
      case Success(users) => userList = users
      case Failure(e) => print("error while listing users", e)
    }
    addressRepository.list().onComplete {
      case Success(address) => addressList = address
      case Failure(e) => print("error while listing addresses", e)
    }
    paymentRepository.list().onComplete {
      case Success(payment) => paymentList = payment
      case Failure(e) => print("error while listing payments", e)
    }
    voucherRepository.list().onComplete {
      case Success(voucher) => voucherList = voucher
      case Failure(e) => print("error while listing vouchers", e)
    }
  }

  var userList: Seq[User] = Seq[User]()
  var addressList: Seq[UserAddress] = Seq[UserAddress]()
  var paymentList: Seq[Payment] = Seq[Payment]()
  var voucherList: Seq[Voucher] = Seq[Voucher]()
  fetchData()

  def listOrders: Action[AnyContent] = Action.async { implicit request =>
    orderRepo.list().map(orders => Ok(views.html.order_list(orders)))
  }

  def createOrder(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = Await.result(userRepo.list(), 1.second)
    val addresses = Await.result(addressRepository.list(), 1.second)
    val payments = Await.result(paymentRepository.list(), 1.second)
    val vouchers = voucherRepository.list()

    vouchers.map(vouchers => Ok(views.html.order_create(orderForm, users, addresses, payments, vouchers)))
  }

  def createOrderHandle(): Action[AnyContent] = Action.async { implicit request =>
    orderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.order_create(errorForm, userList, addressList, paymentList, voucherList))
        )
      },
      order => {
        orderRepo.create(order.userId, order.addressId, order.paymentId, order.voucherId).map { _ =>
          Redirect(url)
        }
      }
    )
  }

  def updateOrder(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val ord = orderRepo.getByIdOption(id)
    ord.map(order => {
      val prodForm = updateOrderForm.fill(UpdateOrderForm(order.get.id, order.get.userId, order.get.addressId, order.get.paymentId, order.get.voucherId))
      Ok(views.html.order_update(prodForm, userList, addressList, paymentList, voucherList))
    })
  }

  def updateOrderHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateOrderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.order_update(errorForm, userList, addressList, paymentList, voucherList))
        )
      },
      order => {
        orderRepo.update(order.id, Order(order.id, order.userId, order.paymentId, order.voucherId)).map { _ =>
          Redirect(url)
        }
      }
    )
  }

  def deleteOrder(id: Long): Action[AnyContent] = Action {
    orderRepo.delete(id)
    Redirect(url)
  }
}

case class CreateOrderForm(userId: Long, addressId: Long, paymentId: Long, voucherId: Long)

case class UpdateOrderForm(id: Long, addressId: Long, userId: Long, paymentId: Long, voucherId: Long)
