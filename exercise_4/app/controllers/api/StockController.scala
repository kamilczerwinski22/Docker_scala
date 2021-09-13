package controllers.api

import akka.actor.ActorSystem
import javax.inject.Inject
import models.Stock
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.StockRepository

import scala.concurrent.{ExecutionContext, Future}

class StockController @Inject()(val stockRepo: StockRepository,
                                cc: ControllerComponents, actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends AbstractController(cc) {


  def getStockById(id: Long): Action[AnyContent] = Action.async {
    val stock = stockRepo.getByIdOption(id)
    stock.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("stock  cannot be found")
    }
  }

  def listStocks(): Action[AnyContent] = Action.async {
    val stocks = stockRepo.list()
    stocks.map { stocks =>
      Ok(Json.toJson(stocks))
    }
  }

  def createStock(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Stock].map {
      stock =>
        stockRepo.create(stock.unitPrice, stock.totalPrice, stock.totalStock).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("incorrect data provided")))
  }

  def updateStock(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Stock].map {
      stock =>
        stockRepo.update(stock.id, stock).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteStock(id: Long): Action[AnyContent] = Action.async {
    stockRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
