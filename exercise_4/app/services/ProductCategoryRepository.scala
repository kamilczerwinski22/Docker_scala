package services

import models.{ProductCategory}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import java.sql.Timestamp
import java.time.Instant
import java.util.Date

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductCategoryRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CategoryTable(tag: Tag) extends Table[ProductCategory](tag, "category") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def * = (id, name) <> ((ProductCategory.apply _).tupled, ProductCategory.unapply)
  }

  val category = TableQuery[CategoryTable]

  def create(name: String): Future[ProductCategory] = db.run {
    (category.map(ctg => (ctg.name))
      returning category.map(_.id)
      into { case ((name), id) => ProductCategory(id, name) }
      ) += (name)
  }

  def getByIdOption(id: Long): Future[Option[ProductCategory]] = db.run {
    category.filter(_.id === id).result.headOption
  }

  def getByNameOption(name: String): Future[Option[ProductCategory]] = db.run {
    category.filter(_.name === name).result.headOption
  }

  def list(): Future[Seq[ProductCategory]] = db.run {
    category.result
  }

  def update(id: Long, newCategory: ProductCategory): Future[Int] = {
    val categoryToUpdate: ProductCategory = newCategory.copy(id)
    db.run(category.filter(_.id === id).update(categoryToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(category.filter(_.id === id).delete)
}
