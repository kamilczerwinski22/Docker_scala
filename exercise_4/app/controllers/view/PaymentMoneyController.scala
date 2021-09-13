package controllers.view

import models.{PaymentMoney, User}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.{PaymentMoneyRepository, UserRepository}
import javax.inject._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class PaymentMoneyController @Inject()(paymentMoneyRepo: PaymentMoneyRepository,
                                       userRepo: UserRepository,
                                       cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  var userList: Seq[User] = Seq[User]()

  fetchData()

  def listPaymentMoneys: Action[AnyContent] = Action.async { implicit request =>
    paymentMoneyRepo.list().map(creditCards => Ok(views.html.payment_money_list(creditCards)))
  }

  def createPaymentMoney(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = userRepo.list()

    users.map(users => Ok(views.html.payment_money_create(paymentMoneyForm, users)))
  }

  def createPaymentMoneyHandle(): Action[AnyContent] = Action.async { implicit request =>
    paymentMoneyForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.payment_money_create(errorForm, userList))
        )
      },
      paymentMoney => {
        paymentMoneyRepo.create(paymentMoney.userId, paymentMoney.cardholderName).map { _ =>
          Redirect("/form/payment-money/list")
        }
      }
    )
  }

  def updatePaymentMoney(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val paymentMoney = paymentMoneyRepo.getByIdOption(id)
    paymentMoney.map(paymentMoney => {
      val prodForm = updatePaymentMoneyForm.fill(UpdatePaymentMoneyForm(paymentMoney.get.id, paymentMoney.get.userId, paymentMoney.get.cardholderName))
      Ok(views.html.payment_money_update(prodForm, userList))
    })
  }

  def updatePaymentMoneyHandle(): Action[AnyContent] = Action.async { implicit request =>
    updatePaymentMoneyForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.payment_money_update(errorForm, userList))
        )
      },
      creditCard => {
        paymentMoneyRepo.update(creditCard.id, PaymentMoney(creditCard.id, creditCard.userId, creditCard.cardholderName)).map { _ =>
          Redirect("/form/payment-money/list")
        }
      }
    )
  }

  def deletePaymentMoney(id: Long): Action[AnyContent] = Action {
    paymentMoneyRepo.delete(id)
    Redirect("/form/payment-money/list")
  }


  val paymentMoneyForm: Form[CreatePaymentMoneyForm] = Form {
    mapping(
      "userId" -> longNumber,
      "cardholderName" -> nonEmptyText,
    )(CreatePaymentMoneyForm.apply)(CreatePaymentMoneyForm.unapply)
  }

  val updatePaymentMoneyForm: Form[UpdatePaymentMoneyForm] = Form {
    mapping(
      "id" -> longNumber,
      "userId" -> longNumber,
      "cardholderName" -> nonEmptyText,
    )(UpdatePaymentMoneyForm.apply)(UpdatePaymentMoneyForm.unapply)
  }

  def fetchData(): Unit = {
    userRepo.list().onComplete {
      case Success(user) => userList = user
      case Failure(e) => print("error while listing users", e)
    }
  }
}

case class CreatePaymentMoneyForm(userId: Long, cardholderName: String)

case class UpdatePaymentMoneyForm(id: Long, userId: Long, cardholderName: String)