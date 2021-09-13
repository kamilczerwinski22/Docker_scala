package controllers.view

import models.Stock
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.StockRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StockController @Inject()(stockRepo: StockRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def listStocks: Action[AnyContent] = Action.async { implicit request =>
    stockRepo.list().map(stocks => Ok(views.html.stock_list(stocks)))
  }

  def createStock(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val stocks = stockRepo.list()
    stocks.map(_ => Ok(views.html.stock_create(stockForm)))
  }

  def createStockHandle(): Action[AnyContent] = Action.async { implicit request =>
    stockForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.stock_create(errorForm))
        )
      },
      stock => {
        stockRepo.create(stock.unitPrice, stock.totalPrice, stock.totalStock).map { _ =>
          Redirect("/form/stock/list")
        }
      }
    )
  }

  def updateStockHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateStockForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.stock_update(errorForm))
        )
      },
      stock => {
        stockRepo.update(stock.id, Stock(stock.id, stock.unitPrice, stock.totalPrice, stock.totalStock)).map { _ =>
          Redirect("/form/stock/list")
        }
      }
    )
  }

  def deleteStock(id: Long): Action[AnyContent] = Action {
    stockRepo.delete(id)
    Redirect("/form/stock/list")
  }

  def updateStock(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val stock = stockRepo.getByIdOption(id)
    stock.map(stock => {
      val prodForm = updateStockForm.fill(UpdateStockForm(stock.get.id, stock.get.unitPrice, stock.get.totalPrice, stock.get.totalStock))
      Ok(views.html.stock_update(prodForm))
    })
  }

  val stockForm: Form[CreateStockForm] = Form {
    mapping(
      "unitPrice" -> number,
      "totalPrice" -> number,
      "totalStock" -> number,
    )(CreateStockForm.apply)(CreateStockForm.unapply)
  }

  val updateStockForm: Form[UpdateStockForm] = Form {
    mapping(
      "id" -> longNumber,
      "unitPrice" -> number,
      "totalPrice" -> number,
      "totalStock" -> number,
    )(UpdateStockForm.apply)(UpdateStockForm.unapply)
  }

}

case class CreateStockForm(unitPrice: Int, totalPrice: Int, totalStock: Int)

case class UpdateStockForm(id: Long, unitPrice: Int, totalPrice: Int, totalStock: Int)
