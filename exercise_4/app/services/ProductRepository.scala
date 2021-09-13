package services

import models.Product
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import java.time.Instant
import java.util.Date
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductRepository @Inject()(dbConfigProvider: DatabaseConfigProvider,
                                  val stockRepository: StockRepository,
                                  val productCategoryRepository: ProductCategoryRepository,
                                  val productSubcategoryRepository: ProductSubcategoryRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class ProductTable(tag: Tag) extends Table[Product](tag, "product") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def stockId = column[Long]("stock_id")

    def stock_fk = foreignKey("stock_fk", stockId, stock)(_.id)

    def categoryId = column[Long]("category_id")

    def category_fk = foreignKey("category_id_fk", categoryId, category)(_.id)

    def subcategoryId = column[Long]("subcategory_id", O.Default(0))

    def subcategory_fk = foreignKey("subcategory_id_fk", subcategoryId, subcategory)(_.id)

    def description: Rep[String] = column[String]("description")

    def name = column[String]("name")

    def * = (id, stockId, categoryId, subcategoryId, name, description) <> ((Product.apply _).tupled, Product.unapply)
  }

  import productCategoryRepository.CategoryTable
  import stockRepository.StockTable
  import productSubcategoryRepository.ProductSubcategoryTable

  val product = TableQuery[ProductTable]
  val stock = TableQuery[StockTable]
  val category = TableQuery[CategoryTable]
  val subcategory = TableQuery[ProductSubcategoryTable]

  def create(stockId: Long, categoryId: Long, subcategoryId: Long, name: String, description: String): Future[Product] = db.run {
    (product.map(p => (p.stockId, p.categoryId, p.subcategoryId, p.name, p.name))
      returning product.map(_.id)
      into { case ((stockId, categoryId, subcategoryId, name, description), id) => Product(id, stockId, categoryId, subcategoryId, name, description) }
      ) += (stockId, categoryId, subcategoryId, name, description)
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
