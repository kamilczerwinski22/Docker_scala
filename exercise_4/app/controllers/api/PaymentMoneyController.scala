package controllers.api

import models.PaymentMoney
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.{PaymentMoneyRepository}
import javax.inject._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentMoneyController @Inject()(val paymentMoneyRepo: PaymentMoneyRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {
  

  def getPaymentMoneyById(id: Long): Action[AnyContent] = Action.async {
    val paymentMoney = paymentMoneyRepo.getByIdOption(id)
    paymentMoney.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("creditCard with given id cannot be found")
    }
  }

  def listPaymentMoneys(): Action[AnyContent] = Action.async {

    val paymentMoneys = paymentMoneyRepo.list()
    paymentMoneys.map { creditCards =>
      Ok(Json.toJson(creditCards))
    }
  }

  def listPaymentMoneysByUserId(userId: Long): Action[AnyContent] = Action.async {
    val paymentMoneys = paymentMoneyRepo.listByUserId(userId)
    paymentMoneys.map { creditCards =>
      Ok(Json.toJson(creditCards))
    }
  }

  def createPaymentMoney(): Action[JsValue] = Action.async(parse.json) { implicit request =>

    request.body.validate[PaymentMoney].map {
      paymentMoney =>
        paymentMoneyRepo.create(paymentMoney.userId, paymentMoney.cardholderName).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("incorrect data have been provided")))
  }

  def updatePaymentMoney(): Action[JsValue] = Action.async(parse.json) { request =>

    request.body.validate[PaymentMoney].map {
      paymentMoney =>
        paymentMoneyRepo.update(paymentMoney.id, paymentMoney).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deletePaymentMoney(id: Long): Action[AnyContent] = Action.async {
    paymentMoneyRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}