package controllers.view

import models.{Product, ProductCategory, ProductSubcategory, Stock}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.{ProductCategoryRepository, ProductRepository, ProductSubcategoryRepository, StockRepository}
import javax.inject._

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class ProductController @Inject()(productRepo: ProductRepository,
                                  stockRepo: StockRepository,
                                  categoryRepo: ProductCategoryRepository,
                                  subcategoryRepo: ProductSubcategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  var stockList: Seq[Stock] = Seq[Stock]()
  var categoryList: Seq[ProductCategory] = Seq[ProductCategory]()
  var subcategoryList: Seq[ProductSubcategory] = Seq[ProductSubcategory]()
  fetchData()

  def listProducts: Action[AnyContent] = Action.async { implicit request =>
    productRepo.list().map(products => Ok(views.html.product_list(products)))
  }

  def createProduct(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val stocks = Await.result(stockRepo.list(), 1.second)
    val categories = Await.result(categoryRepo.list(), 1.second)
    val subcategories = subcategoryRepo.list()

    subcategories.map(subcategory => Ok(views.html.product_create(productForm, stocks, categories, subcategory)))
  }

  def createProductHandle(): Action[AnyContent] = Action.async { implicit request =>
    productForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.product_create(errorForm, stockList, categoryList, subcategoryList))
        )
      },
      product => {
        productRepo.create(product.stockId, product.categoryId, product.subcategoryId, product.name, product.description).map { _ =>
          Redirect("/form/product/list")
        }
      }
    )
  }

  def updateProduct(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val product = productRepo.getByIdOption(id)
    product.map(product => {
      val prodForm = updateProductForm.fill(UpdateProductForm(product.get.id, product.get.stockId, product.get.categoryId, product.get.subcategoryId, product.get.name, product.get.description))
      Ok(views.html.product_update(prodForm, stockList, categoryList, subcategoryList))
    })
  }

  def updateProductHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateProductForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.product_update(errorForm, stockList, categoryList, subcategoryList))
        )
      },
      product => {
        productRepo.update(product.id, Product(product.id, product.stockId, product.categoryId, product.subcategoryId, product.name, product.description)).map { _ =>
          Redirect("/form/product/list")
        }
      }
    )
  }

  def deleteProduct(id: Long): Action[AnyContent] = Action {
    productRepo.delete(id)
    Redirect("/form/product/list")
  }


  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "stockId" -> longNumber,
      "categoryId" -> longNumber,
      "subcategoryId" -> longNumber,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  val updateProductForm: Form[UpdateProductForm] = Form {
    mapping(
      "id" -> longNumber,
      "stockId" -> longNumber,
      "categoryId" -> longNumber,
      "subcategoryId" -> longNumber,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
    )(UpdateProductForm.apply)(UpdateProductForm.unapply)
  }

  def fetchData(): Unit = {

    stockRepo.list().onComplete {
      case Success(stocks) => stockList = stocks
      case Failure(e) => print("error while listing stocks", e)
    }

    categoryRepo.list().onComplete {
      case Success(category) => categoryList = category
      case Failure(e) => print("error while listing categories", e)
    }

    subcategoryRepo.list().onComplete {
      case Success(subcategory) => subcategoryList = subcategory
      case Failure(e) => print("error while listing subcategories", e)
    }
  }
}

case class CreateProductForm(stockId: Long, categoryId: Long, subcategoryId: Long, name: String, description: String)

case class UpdateProductForm(id: Long, stockId: Long, categoryId: Long, subcategoryId: Long, name: String, description: String)