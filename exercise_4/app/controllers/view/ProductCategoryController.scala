package controllers.view

import models.ProductCategory
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.ProductCategoryRepository
import javax.inject._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductCategoryController @Inject()(categoryRepo: ProductCategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def listCategories: Action[AnyContent] = Action.async { implicit request =>
    categoryRepo.list().map(categories => Ok(views.html.category_list(categories)))
  }

  def createCategory(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val categories = categoryRepo.list()
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
        categoryRepo.create(category.name).map { _ =>
          Redirect("/form/category/list")
        }
      }
    )
  }

  def updateCategory(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val category = categoryRepo.getByIdOption(id)
    category.map(category => {
      val prodForm = updateCategoryForm.fill(UpdateProductCategoryForm(category.get.id, category.get.name))
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
        categoryRepo.update(category.id, ProductCategory(category.id, category.name)).map { _ =>
          Redirect("/form/category/list")
        }
      }
    )
  }

  def deleteCategory(id: Long): Action[AnyContent] = Action {
    categoryRepo.delete(id)
    Redirect("/form/category/list")
  }

  // utilities

  val categoryForm: Form[CreateProductCategoryForm] = Form {
    mapping(
      "name" -> nonEmptyText,
    )(CreateProductCategoryForm.apply)(CreateProductCategoryForm.unapply)
  }

  val updateCategoryForm: Form[UpdateProductCategoryForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
    )(UpdateProductCategoryForm.apply)(UpdateProductCategoryForm.unapply)
  }
}

case class CreateProductCategoryForm(name: String)

case class UpdateProductCategoryForm(id: Long, name: String)