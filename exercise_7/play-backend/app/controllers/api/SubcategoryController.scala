package controllers.api

import models.Subcategory

import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.SubcategoryRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubcategoryController @Inject()(val subcategoryRepo: SubcategoryRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def getSubcategoryById(id: Long): Action[AnyContent] = Action.async {

    val subcategory = subcategoryRepo.getByIdOption(id)
    subcategory.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("subcategory with given id cannot be found")
    }
  }

  def getSubcategoryByName(name: String): Action[AnyContent] = Action.async {

    val subcategory = subcategoryRepo.getByNameOption(name)
    subcategory.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("subcategory with given name cannot be found")
    }
  }

  def listSubcategories(): Action[AnyContent] = Action.async {

    val subcategories = subcategoryRepo.list()
    subcategories.map { subcategories =>
      Ok(Json.toJson(subcategories))
    }
  }

  def listSubcategoriesByCategoryId(categoryId: Long): Action[AnyContent] = Action.async {

    val subcategories = subcategoryRepo.listByCategoryId(categoryId)
    subcategories.map { subcategories =>
      Ok(Json.toJson(subcategories))
    }
  }

  def createSubcategory(): Action[JsValue] = Action.async(parse.json) { implicit request =>

    request.body.validate[Subcategory].map {
      subcategory =>
        subcategoryRepo.create(subcategory.name, subcategory.categoryId).map { res =>

          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("incorrect data have been provided")))
  }

  def updateSubcategory(): Action[JsValue] = Action.async(parse.json) { request =>


    request.body.validate[Subcategory].map {
      subcategory =>
        subcategoryRepo.update(subcategory.id, subcategory).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteSubcategory(id: Long): Action[AnyContent] = Action.async {
    subcategoryRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
