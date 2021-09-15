package controllers.api

import models.CreditCard
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.CreditCardRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreditCardController @Inject()(val creditCardRepository: CreditCardRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def getCreditCardById(id: Long): Action[AnyContent] = Action.async {
    val creditCard = creditCardRepository.getByIdOption(id)
    creditCard.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("creditCard with given id cannot be found")
    }
  }

  def listCreditCards(): Action[AnyContent] = Action.async {
    val creditCards = creditCardRepository.list()
    creditCards.map { creditCards =>
      Ok(Json.toJson(creditCards))
    }
  }

  def listCreditCardsByUserId(userId: Long): Action[AnyContent] = Action.async {
    val creditCards = creditCardRepository.listByUserId(userId)
    creditCards.map { creditCards =>
      Ok(Json.toJson(creditCards))
    }
  }

  def createCreditCard(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CreditCard].map {
      creditCard =>
        creditCardRepository.create(creditCard.userId, creditCard.cardholderName, creditCard.number, creditCard.expDate, creditCard.cvcCode).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("incorrect data have been provided")))
  }

  def updateCreditCard(): Action[JsValue] = Action.async(parse.json) { request =>

    request.body.validate[CreditCard].map {
      creditCard =>
        creditCardRepository.update(creditCard.id, creditCard).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteCreditCard(id: Long): Action[AnyContent] = Action.async {

    creditCardRepository.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
