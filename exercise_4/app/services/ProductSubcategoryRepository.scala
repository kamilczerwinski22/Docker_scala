package services

import models.{ProductSubcategory}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import java.sql.Timestamp
import java.time.Instant
import java.util.Date

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductSubcategoryRepository @Inject()(dbConfigProvider: DatabaseConfigProvider,
                                             val productCategoryRepository: ProductCategoryRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  import productCategoryRepository.CategoryTable
  val category = TableQuery[CategoryTable]

  class ProductSubcategoryTable(tag: Tag) extends Table[ProductSubcategory](tag, "subcategory") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def categoryId = column[Long]("category_id")

    def category_fk = foreignKey("category_fk", categoryId, category)(_.id)

    def name = column[String]("name")

    def * = (id, categoryId, name) <> ((ProductSubcategory.apply _).tupled, ProductSubcategory.unapply)
  }


  val subcategory = TableQuery[ProductSubcategoryTable]

  def create(name: String, categoryId: Long): Future[ProductSubcategory] = db.run {
    (subcategory.map(sb => (sb.name, sb.categoryId))
      returning subcategory.map(_.id)
      into { case ((name, categoryId), id) => ProductSubcategory(id, categoryId, name) }
      ) += (name, categoryId)
  }

  def getByIdOption(id: Long): Future[Option[ProductSubcategory]] = db.run {
    subcategory.filter(_.id === id).result.headOption
  }

  def getByNameOption(name: String): Future[Option[ProductSubcategory]] = db.run {
    subcategory.filter(_.name === name).result.headOption
  }

  def list(): Future[Seq[ProductSubcategory]] = db.run {
    subcategory.result
  }

  def listByCategoryId(categoryId: Long): Future[Seq[ProductSubcategory]] = db.run {
    subcategory.filter(_.categoryId === categoryId).result
  }

  def update(id: Long, newSubcategory: ProductSubcategory): Future[Int] = {
    val subcategoryToUpdate: ProductSubcategory = newSubcategory.copy(id)
    db.run(subcategory.filter(_.id === id).update(subcategoryToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(subcategory.filter(_.id === id).delete)
}
