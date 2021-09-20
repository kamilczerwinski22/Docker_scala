package controllers.view

import models.{CreditCard, Payment, User}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import repositories.{CreditCardRepository, PaymentRepository, UserRepository}

import javax.inject._
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class PaymentController @Inject()(paymentRepo: PaymentRepository,
                                  userRepository: UserRepository,
                                  creditCardRepository: CreditCardRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val url = "/form/payment/list"

  val paymentForm: Form[CreatePaymentForm] = Form {
    mapping(
      "userId" -> longNumber,
      "creditCardId" -> longNumber,
      "amount" -> number,
    )(CreatePaymentForm.apply)(CreatePaymentForm.unapply)
  }

  val updatePaymentForm: Form[UpdatePaymentForm] = Form {
    mapping(
      "id" -> longNumber,
      "userId" -> longNumber,
      "creditCardId" -> longNumber,
      "amount" -> number,
    )(UpdatePaymentForm.apply)(UpdatePaymentForm.unapply)
  }

  def fetchData(): Unit = {
    userRepository.list().onComplete {
      case Success(users) => userList = users
      case Failure(e) => print("error while listing users", e)
    }
    creditCardRepository.list().onComplete {
      case Success(creditCards) => creditCardList = creditCards
      case Failure(e) => print("error while listing creditCards", e)
    }
  }

  var userList: Seq[User] = Seq[User]()
  var creditCardList: Seq[CreditCard] = Seq[CreditCard]()

  fetchData()

  def listPayments: Action[AnyContent] = Action.async { implicit request =>
    paymentRepo.list().map(payments => Ok(views.html.payment_list(payments)))
  }

  def createPayment(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = Await.result(userRepository.list(), 1.second)
    val creditCards = creditCardRepository.list()

    creditCards.map(creditCards => Ok(views.html.payment_create(paymentForm, users, creditCards)))
  }

  def createPaymentHandle(): Action[AnyContent] = Action.async { implicit request =>
    paymentForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.payment_create(errorForm, userList, creditCardList))
        )
      },
      payment => {
        paymentRepo.create(payment.userId, payment.creditCardId, payment.amount).map { _ =>
          Redirect(url)
        }
      }
    )
  }

  def updatePayment(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val payment = paymentRepo.getByIdOption(id)
    payment.map(payment => {
      val prodForm = updatePaymentForm.fill(UpdatePaymentForm(payment.get.id, payment.get.userId, payment.get.creditCardId, payment.get.amount))
      Ok(views.html.payment_update(prodForm, userList, creditCardList))
    })
  }

  def updatePaymentHandle(): Action[AnyContent] = Action.async { implicit request =>
    updatePaymentForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.payment_update(errorForm, userList, creditCardList))
        )
      },
      payment => {
        paymentRepo.update(payment.id, Payment(payment.id, payment.userId, payment.creditCardId, payment.amount)).map { _ =>
          Redirect(url)
        }
      }
    )
  }

  def deletePayment(id: Long): Action[AnyContent] = Action {
    paymentRepo.delete(id)
    Redirect(url)
  }
}

case class CreatePaymentForm(userId: Long, creditCardId: Long, amount: Int)

case class UpdatePaymentForm(id: Long, userId: Long, creditCardId: Long, amount: Int)
