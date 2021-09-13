package controllers.view

import models.{PaymentMoney, Payment, User}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.{PaymentMoneyRepository, PaymentRepository, UserRepository}
import javax.inject._

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class PaymentController @Inject()(paymentRepo: PaymentRepository,
                                  userRepo: UserRepository,
                                  paymentMoneyRepo: PaymentMoneyRepository,
                                  cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  var userList: Seq[User] = Seq[User]()
  var paymentMoneysList: Seq[PaymentMoney] = Seq[PaymentMoney]()
  fetchData()


  def listPayments: Action[AnyContent] = Action.async { implicit request =>
    paymentRepo.list().map(payments => Ok(views.html.payment_list(payments)))
  }

  def createPayment(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = Await.result(userRepo.list(), 1.second)
    val paymentMoneys = paymentMoneyRepo.list()

    paymentMoneys.map(creditCards => Ok(views.html.payment_create(paymentForm, users, creditCards)))
  }

  def createPaymentHandle(): Action[AnyContent] = Action.async { implicit request =>
    paymentForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.payment_create(errorForm, userList, paymentMoneysList))
        )
      },
      payment => {
        paymentRepo.create(payment.userId, payment.paymentMoneyId, payment.amount).map { _ =>
          Redirect("/form/payment/list")
        }
      }
    )
  }

  def updatePayment(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val payment = paymentRepo.getByIdOption(id)
    payment.map(payment => {
      val prodForm = updatePaymentForm.fill(UpdatePaymentForm(payment.get.id, payment.get.userId, payment.get.paymentMoneyId, payment.get.amount))
      Ok(views.html.payment_update(prodForm, userList, paymentMoneysList))
    })
  }

  def updatePaymentHandle(): Action[AnyContent] = Action.async { implicit request =>
    updatePaymentForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.payment_update(errorForm, userList, paymentMoneysList))
        )
      },
      payment => {
        paymentRepo.update(payment.id, Payment(payment.id, payment.userId, payment.paymentMoneyId, payment.amount)).map { _ =>
          Redirect("/form/payment/list")
        }
      }
    )
  }

  def deletePayment(id: Long): Action[AnyContent] = Action {
    paymentRepo.delete(id)
    Redirect("/form/payment/list")
  }

  // utilities

  val paymentForm: Form[CreatePaymentForm] = Form {
    mapping(
      "userId" -> longNumber,
      "paymentMoneyId" -> longNumber,
      "amount" -> number,
    )(CreatePaymentForm.apply)(CreatePaymentForm.unapply)
  }

  val updatePaymentForm: Form[UpdatePaymentForm] = Form {
    mapping(
      "id" -> longNumber,
      "userId" -> longNumber,
      "paymentMoneyId" -> longNumber,
      "amount" -> number,
    )(UpdatePaymentForm.apply)(UpdatePaymentForm.unapply)
  }

  def fetchData(): Unit = {

    userRepo.list().onComplete {
      case Success(users) => userList = users
      case Failure(e) => print("error while listing users", e)
    }

    paymentMoneyRepo.list().onComplete {
      case Success(paymentMoneys) => paymentMoneysList = paymentMoneys
      case Failure(e) => print("error while listing paymentmoneys", e)
    }
  }
}

case class CreatePaymentForm(userId: Long, paymentMoneyId: Long, amount: Int)

case class UpdatePaymentForm(id: Long, userId: Long, paymentMoneyId: Long, amount: Int)
