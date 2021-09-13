package controllers.api

import akka.actor.ActorSystem
import javax.inject.Inject
import models.Payment
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.PaymentRepository

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration._

class PaymentController @Inject()(val paymentRepo: PaymentRepository,
                                  cc: ControllerComponents, actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getPaymentById(id: Long): Action[AnyContent] = Action.async {
    val payment = paymentRepo.getByIdOption(id)
    payment.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("payment cannot be found")
    }
  }

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

  def listPaymentsByPaymentMoneyId(paymentMoneyId: Long): Action[AnyContent] = Action.async {
    val payments = paymentRepo.listByPaymentMoneyId(paymentMoneyId)
    payments.map { payments =>
      Ok(Json.toJson(payments))
    }
  }

  def createPayment(): Action[JsValue] = Action.async(parse.json) { implicit request =>

    request.body.validate[Payment].map {
      payment =>
        paymentRepo.create(payment.userId, payment.paymentMoneyId, payment.amount).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("incorrect data have been provided")))
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
