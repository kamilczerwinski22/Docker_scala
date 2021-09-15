package controllers.api

import models.Category
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.CategoryRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryController @Inject()(val categoryRepository: CategoryRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {


  def getCategoryById(id: Long): Action[AnyContent] = Action.async {

    val category = categoryRepository.getByIdOption(id)
    category.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("category with given id cannot be found")
    }
  }

  def getCategoryByName(name: String): Action[AnyContent] = Action.async {
    val category = categoryRepository.getByNameOption(name)
    category.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("category with given name cannot be found")
    }
  }

  def listCategories(): Action[AnyContent] = Action.async {
    val categories = categoryRepository.list()
    categories.map { categories =>
      Ok(Json.toJson(categories))
    }
  }

  def createCategory(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Category].map {
      category =>
        categoryRepository.create(category.name).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("incorrect data have been provided")))
  }

  def updateCategory(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Category].map {
      category =>
        categoryRepository.update(category.id, category).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteCategory(id: Long): Action[AnyContent] = Action.async {

    categoryRepository.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}

