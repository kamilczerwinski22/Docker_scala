package services

import javax.inject.{Inject, Singleton}
import models.Product
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ProductRepository  @Inject()(dbConfigProvider: DatabaseConfigProvider,
                                   val productStockRepository: ProductStockRepository,
                                   val productCategoryRepository: ProductCategoryRepository,
                                   val productSubcategoryRepository: ProductSubcategoryRepository,
                                   val productDescriptionRepository: ProductDescriptionRepository)
                                  (implicit ec: ExecutionContext){

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  import productStockRepository.ProductStockTable
  import productCategoryRepository.ProductCategoryTable
  import productSubcategoryRepository.ProductSubcategoryTable
  import productDescriptionRepository.ProductDescriptionTable

  val stock = TableQuery[ProductStockTable]
  val category = TableQuery[ProductCategoryTable]
  val subcategory = TableQuery[ProductSubcategoryTable]
  val description = TableQuery[ProductDescriptionTable]

  class ProductTable(tag: Tag) extends Table[Product](tag, "product") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def stockId = column[Long]("stock_id")
    def stockFk = foreignKey("stock_fk", stockId, stock)(_.id)

    def categoryId = column[Long]("category_id")
    def categoryFk = foreignKey("category_id_fk", categoryId, category)(_.id)

    def subcategoryId = column[Long]("subcategory_id", O.Default(0))
    def subcategoryFk = foreignKey("subcategory_id_fk", subcategoryId, subcategory)(_.id)

    def descriptionId = column[Long]("description_id", O.Default(0))
    def descriptionFk = foreignKey("description_id_fk", descriptionId, description)(_.id)

    def name = column[String]("name")

    def * = (id, stockId, categoryId, subcategoryId, descriptionId, name) <> ((Product.apply _).tupled, Product.unapply)
  }

  val product = TableQuery[ProductTable]


  def create(stockId: Long, categoryId: Long, subcategoryId: Long, descriptionId:Long, name: String): Future[Product] = db.run {
    (product.map(p => (p.stockId, p.categoryId, p.subcategoryId, p.descriptionId, p.name))
      returning product.map(_.id)
      into {case ((stockId, categoryId, subcategoryId, descriptionId, name), id) => Product(id, stockId, categoryId, subcategoryId, descriptionId, name)}
      ) += (stockId, categoryId, subcategoryId, descriptionId, name)
  }

  def getById(id: Long): Future[Product] = db.run {
    product.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Product]] = db.run {
    product.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[Product]] = db.run {
    product.result
  }

  def listByIds(ids: Seq[Long]): Future[Seq[Product]] = db.run {
    product.filter(_.id.inSet(ids)).result
  }

  def listByStockId(stockId: Long): Future[Seq[Product]] = db.run {
    product.filter(_.stockId === stockId).result
  }

  def listByCategoryId(categoryId: Long): Future[Seq[Product]] = db.run {
    product.filter(_.categoryId === categoryId).result
  }

  def listBySubcategoryId(subcategoryId: Long): Future[Seq[Product]] = db.run {
    product.filter(_.subcategoryId === subcategoryId).result
  }

  def update(id: Long, newProduct: Product): Future[Int] = {
    val productToUpdate: Product = newProduct.copy(id)
    db.run(product.filter(_.id === id).update(productToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(product.filter(_.id === id).delete)

}
