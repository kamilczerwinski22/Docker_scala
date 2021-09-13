package controllers.api

import akka.actor.ActorSystem
import javax.inject.Inject
import models.ProductCategory
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.ProductCategoryRepository

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration._

class ProductCategoryController @Inject()(val categoryRepo: ProductCategoryRepository,
                                          cc: ControllerComponents, actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getCategoryById(id: Long): Action[AnyContent] = Action.async {
    val category = categoryRepo.getByIdOption(id)
    category.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("category cannot be found")
    }
  }

  def getCategoryByName(name: String): Action[AnyContent] = Action.async {
    val category = categoryRepo.getByNameOption(name)
    category.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("category cannot be found")
    }
  }

  def listCategories(): Action[AnyContent] = Action.async {
    val categories = categoryRepo.list()
    categories.map { categories =>
      Ok(Json.toJson(categories))
    }
  }

  def createCategory(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[ProductCategory].map {
      category =>
        categoryRepo.create(category.name).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("incorrect data provided")))
  }

  def updateCategory(): Action[JsValue] = Action.async(parse.json) { request =>

    request.body.validate[ProductCategory].map {
      category =>
        categoryRepo.update(category.id, category).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteCategory(id: Long): Action[AnyContent] = Action.async {
    categoryRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
