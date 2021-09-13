package controllers.view

import models.{ProductCategory, ProductSubcategory}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.{ProductCategoryRepository, ProductSubcategoryRepository}

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class ProductSubcategoryController @Inject()(subcategoryRepo: ProductSubcategoryRepository, categoryRepo: ProductCategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  var categoryList: Seq[ProductCategory] = Seq[ProductCategory]()

  // fill lists
  fetchData()

  def listSubcategories: Action[AnyContent] = Action.async { implicit request =>
    subcategoryRepo.list().map(subcategories => Ok(views.html.subcategory_list(subcategories)))
  }

  def createSubcategory(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val categories = categoryRepo.list()

    categories.map(categories => Ok(views.html.subcategory_create(subcategoryForm, categories)))
  }

  def createSubcategoryHandle(): Action[AnyContent] = Action.async { implicit request =>
    subcategoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.subcategory_create(errorForm, categoryList))
        )
      },
      subcategory => {
        subcategoryRepo.create(subcategory.name, subcategory.categoryId).map { _ =>
          Redirect("/form/subcategory/list")
        }
      }
    )
  }

  def updateSubcategory(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val subcategory = subcategoryRepo.getByIdOption(id)
    subcategory.map(subcategory => {
      val prodForm = updateSubcategoryForm.fill(UpdateProductSubcategoryForm(subcategory.get.id, subcategory.get.categoryId, subcategory.get.name))
      Ok(views.html.subcategory_update(prodForm, categoryList))
    })
  }

  def updateSubcategoryHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateSubcategoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.subcategory_update(errorForm, categoryList))
        )
      },
      subcategory => {
        subcategoryRepo.update(subcategory.id, ProductSubcategory(subcategory.id, subcategory.categoryId, subcategory.name)).map { _ =>
          Redirect("/form/subcategory/list")
        }
      }
    )
  }

  def deleteSubcategory(id: Long): Action[AnyContent] = Action {
    subcategoryRepo.delete(id)
    Redirect("/form/subcategory/list")
  }

  // utilities

  val subcategoryForm: Form[CreateProductSubcategoryForm] = Form {
    mapping(
      "categoryId" -> longNumber,
      "name" -> nonEmptyText,
    )(CreateProductSubcategoryForm.apply)(CreateProductSubcategoryForm.unapply)
  }

  val updateSubcategoryForm: Form[UpdateProductSubcategoryForm] = Form {
    mapping(
      "id" -> longNumber,
      "categoryId" -> longNumber,
      "name" -> nonEmptyText,
    )(UpdateProductSubcategoryForm.apply)(UpdateProductSubcategoryForm.unapply)
  }

  def fetchData(): Unit = {
    categoryRepo.list().onComplete {
      case Success(category) => categoryList = category
      case Failure(e) => print("error while listing categories", e)
    }
  }
}

case class CreateProductSubcategoryForm(categoryId: Long, name: String)

case class UpdateProductSubcategoryForm(id: Long, categoryId: Long, name: String)