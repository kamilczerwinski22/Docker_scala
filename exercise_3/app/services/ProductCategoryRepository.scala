package services

import javax.inject.{Inject, Singleton}
import models.ProductCategory
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductCategoryRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, val categoryRepository: ProductCategoryRepository)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class ProductCategoryTable(tag: Tag) extends Table[ProductCategory](tag, "category") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def * = (id, name) <> ((ProductCategory.apply _).tupled, ProductCategory.unapply)
  }

  val category = TableQuery[ProductCategoryTable]

  def list(): Future[Seq[ProductCategory]] = db.run {
    category.result
  }

  def create(name: String) : Future[ProductCategory] = db.run {
    (category.map(category => (category.name))
      returning category.map(_.id)
      into {case ((name), id) => ProductCategory(id, name)}
      ) += (name)
  }

  def getById(id: Long): Future[ProductCategory] = db.run {
    category.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[ProductCategory]] = db.run {
    category.filter(_.id === id).result.headOption
  }

  def update(id: Long, newCategory: ProductCategory): Future[Int] = {
    val categoryUpdate: ProductCategory = newCategory.copy(id)
    db.run(category.filter(_.id === id).update(categoryUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(category.filter(_.id === id).delete)

}
