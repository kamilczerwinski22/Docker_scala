package controllers.api

import akka.actor.ActorSystem
import javax.inject.Inject
import models.Product
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.ProductRepository

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}

class ProductController @Inject()(val productRepo: ProductRepository,
                                  cc: ControllerComponents, actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getProductById(id: Long): Action[AnyContent] = Action.async {
    val product = productRepo.getByIdOption(id)
    product.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("product_create with given id cannot be found")
    }
  }

  def listProducts(): Action[AnyContent] = Action.async {
    val products = productRepo.list()
    products.map { products =>
      Ok(Json.toJson(products))
    }
  }

  def listProductsByStockId(stockId: Long): Action[AnyContent] = Action.async {
    val products = productRepo.listByStockId(stockId)
    products.map { products =>
      Ok(Json.toJson(products))
    }
  }

  def listProductsByCategoryId(categoryId: Long): Action[AnyContent] = Action.async {
    val products = productRepo.listByCategoryId(categoryId)
    products.map { products =>
      Ok(Json.toJson(products))
    }
  }

  def listProductsBySubcategoryId(subcategoryId: Long): Action[AnyContent] = Action.async {
    val products = productRepo.listBySubcategoryId(subcategoryId)
    products.map { products =>
      Ok(Json.toJson(products))
    }
  }

  def createProduct(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Product].map {
      product =>
        productRepo.create(product.stockId, product.categoryId, product.subcategoryId, product.description, product.name).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("incorrect data have been provided")))
  }

  def updateProduct(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Product].map {
      product =>
        productRepo.update(product.id, product).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteProduct(id: Long): Action[AnyContent] = Action.async {
    productRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
