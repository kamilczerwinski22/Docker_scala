package repositories

import models.Category
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CategoryTable(tag: Tag) extends Table[Category](tag, "category") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (id, name) <> ((Category.apply _).tupled, Category.unapply)
  }

  val category = TableQuery[CategoryTable]

  def list(): Future[Seq[Category]] = db.run {
    category.result
  }

  def create(name: String): Future[Category] = db.run {
    (category.map(c => (c.name))
      returning category.map(_.id)
      into { case ((name), id) => Category(id, name) }
      ) += (name)
  }

  def getByIdOption(id: Long): Future[Option[Category]] = db.run {
    category.filter(_.id === id).result.headOption
  }

  def getByNameOption(name: String): Future[Option[Category]] = db.run {
    category.filter(_.name === name).result.headOption
  }

  def update(id: Long, newCategory: Category): Future[Int] = {
    val categoryToUpdate: Category = newCategory.copy(id)
    db.run(category.filter(_.id === id).update(categoryToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(category.filter(_.id === id).delete)
}
