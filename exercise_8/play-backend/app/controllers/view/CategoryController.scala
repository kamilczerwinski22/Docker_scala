package controllers.view

import models.Category
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import repositories.CategoryRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryController @Inject()(categoryRep: CategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def deleteCategory(id: Long): Action[AnyContent] = Action {
    categoryRep.delete(id)
    Redirect("/form/category/list")
  }

  val categoryForm: Form[CreateCategoryForm] = Form {
    mapping(
      "name" -> nonEmptyText,
    )(CreateCategoryForm.apply)(CreateCategoryForm.unapply)
  }

  val updateCategoryForm: Form[UpdateCategoryForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
    )(UpdateCategoryForm.apply)(UpdateCategoryForm.unapply)
  }

  def listCategories: Action[AnyContent] = Action.async { implicit request =>
    categoryRep.list().map(categories => Ok(views.html.category_list(categories)))
  }

  def createCategory(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val categories = categoryRep.list()
    categories.map(_ => Ok(views.html.category_create(categoryForm)))
  }

  def createCategoryHandle(): Action[AnyContent] = Action.async { implicit request =>
    categoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.category_create(errorForm))
        )
      },
      category => {
        categoryRep.create(category.name).map { _ =>
          Redirect("/form/category/list")
        }
      }
    )
  }

  def updateCategory(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val category = categoryRep.getByIdOption(id)
    category.map(category => {
      val prodForm = updateCategoryForm.fill(UpdateCategoryForm(category.get.id, category.get.name))
      Ok(views.html.category_update(prodForm))
    })
  }

  def updateCategoryHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateCategoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.category_update(errorForm))
        )
      },
      category => {
        categoryRep.update(category.id, Category(category.id, category.name)).map { _ =>
          Redirect("/form/category/list")
        }
      }
    )
  }
}

case class CreateCategoryForm(name: String)

case class UpdateCategoryForm(id: Long, name: String)
