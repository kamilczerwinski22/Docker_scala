package controllers.api

import models.Payment
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import repositories.PaymentRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentController @Inject()(val paymentRepo: PaymentRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def listPayments(): Action[AnyContent] = Action.async {
    val payments = paymentRepo.list()
    payments.map { payments =>
      Ok(Json.toJson(payments))
    }
  }

  def listPaymentsByUserId(userId: Long): Action[AnyContent] = Action.async {
    val payments = paymentRepo.listByUserId(userId)
    payments.map { payments =>
      Ok(Json.toJson(payments))
    }
  }

  def getPaymentById(id: Long): Action[AnyContent] = Action.async {
    val payment = paymentRepo.getByIdOption(id)
    payment.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("payment not found")
    }
  }

  def createPayment(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Payment].map {
      payment =>
        paymentRepo.create(payment.userId, payment.creditCardId, payment.amount).map { res =>

          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("incorrect data")))
  }

  def updatePayment(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Payment].map {
      payment =>
        paymentRepo.update(payment.id, payment).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deletePayment(id: Long): Action[AnyContent] = Action.async {
    paymentRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
