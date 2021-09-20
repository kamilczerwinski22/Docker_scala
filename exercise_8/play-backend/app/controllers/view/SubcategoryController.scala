package controllers.view

import models.{Category, Subcategory}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import repositories.{CategoryRepository, SubcategoryRepository}

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class SubcategoryController @Inject()(subcategoryRepo: SubcategoryRepository, categoryRepo: CategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val url = "/form/subcategory/list"

  val subcategoryForm: Form[CreateSubcategoryForm] = Form {
    mapping(
      "categoryId" -> longNumber,
      "name" -> nonEmptyText,
    )(CreateSubcategoryForm.apply)(CreateSubcategoryForm.unapply)
  }

  val updateSubcategoryForm: Form[UpdateSubcategoryForm] = Form {
    mapping(
      "id" -> longNumber,
      "categoryId" -> longNumber,
      "name" -> nonEmptyText,
    )(UpdateSubcategoryForm.apply)(UpdateSubcategoryForm.unapply)
  }

  def fetchData(): Unit = {
    categoryRepo.list().onComplete {
      case Success(category) => categoryList = category
      case Failure(e) => print("error while listing categories", e)
    }
  }

  var categoryList: Seq[Category] = Seq[Category]()
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
          Redirect(url)
        }
      }
    )
  }

  def updateSubcategory(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val subcategory = subcategoryRepo.getByIdOption(id)
    subcategory.map(subcategory => {
      val prodForm = updateSubcategoryForm.fill(UpdateSubcategoryForm(subcategory.get.id, subcategory.get.categoryId, subcategory.get.name))
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
        subcategoryRepo.update(subcategory.id, Subcategory(subcategory.id, subcategory.categoryId, subcategory.name)).map { _ =>
          Redirect(url)
        }
      }
    )
  }

  def deleteSubcategory(id: Long): Action[AnyContent] = Action {
    subcategoryRepo.delete(id)
    Redirect(url)
  }
}

case class CreateSubcategoryForm(categoryId: Long, name: String)

case class UpdateSubcategoryForm(id: Long, categoryId: Long, name: String)
