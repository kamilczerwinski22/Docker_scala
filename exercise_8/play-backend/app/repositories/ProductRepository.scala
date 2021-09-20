package repositories

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
                                  val categoryRepository: CategoryRepository,
                                  val subcategoryRepository: SubcategoryRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  import categoryRepository.CategoryTable
  import stockRepository.StockTable
  import subcategoryRepository.SubcategoryTable
  val stock = TableQuery[StockTable]
  val category = TableQuery[CategoryTable]
  val subcategory = TableQuery[SubcategoryTable]

  class ProductTable(tag: Tag) extends Table[Product](tag, "product") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def stockId = column[Long]("stock_id")
    def stockFk = foreignKey("stock_fk", stockId, stock)(_.id)
    def categoryId = column[Long]("category_id")
    def categoryFk = foreignKey("category_id_fk", categoryId, category)(_.id)
    def subcategoryId = column[Long]("subcategory_id", O.Default(0))
    def subcategoryFk = foreignKey("subcategory_id_fk", subcategoryId, subcategory)(_.id)
    def name = column[String]("name")
    def imageUrl = column[String]("image_url")
    def description = column[String]("description")


    def * = (id, stockId, categoryId, subcategoryId, name, imageUrl, description) <> ((Product.apply _).tupled, Product.unapply)
  }

  val product = TableQuery[ProductTable]

  def list(): Future[Seq[Product]] = db.run {
    product.result
  }

  def listByIds(ids: Seq[Long]): Future[Seq[Product]] = db.run {
    product.filter(_.id.inSet(ids)).result
  }

  def listByStockId(stockId: Long): Future[Seq[Product]] = db.run {
    product.filter(_.stockId === stockId).result
  }

  def create(stockId: Long, categoryId: Long, subcategoryId: Long, name: String, imageUrl: String, description: String): Future[Product] = db.run {
    (product.map(p => (p.stockId, p.categoryId, p.subcategoryId, p.name, p.imageUrl, p.description))
      returning product.map(_.id)
      into { case ((stockId, categoryId, subcategoryId, name, imageUrl, description), id) => Product(id, stockId, categoryId, subcategoryId, name, imageUrl, description) }
      ) += (stockId, categoryId, subcategoryId, name, imageUrl, description)
  }

  def getByIdOption(id: Long): Future[Option[Product]] = db.run {
    product.filter(_.id === id).result.headOption
  }

  def update(id: Long, newProduct: Product): Future[Int] = {
    val productToUpdate: Product = newProduct.copy(id)
    db.run(product.filter(_.id === id).update(productToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(product.filter(_.id === id).delete)
}
