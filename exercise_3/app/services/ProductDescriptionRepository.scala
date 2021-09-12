package services

import javax.inject.{Inject, Singleton}
import models.ProductDescription
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ProductDescriptionRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  class ProductDescriptionTable(tag: Tag) extends Table[ProductDescription](tag, "description") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def description = column[String]("description")

    def * = (id, description) <> ((ProductDescription.apply _).tupled, ProductDescription.unapply)
  }

  val descriptionTab = TableQuery[ProductDescriptionTable]

  def list(): Future[Seq[ProductDescription]] = db.run {
    descriptionTab.result
  }

  def create(description: String) : Future[ProductDescription] = db.run {
    (descriptionTab.map(dsc => (dsc.description))
      returning descriptionTab.map(_.id)
      into {case ((description), id) => ProductDescription(id, description )}
      ) += (description)
  }

  def getById(id: Long): Future[ProductDescription] = db.run {
    descriptionTab.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[ProductDescription]] = db.run {
    descriptionTab.filter(_.id === id).result.headOption
  }

  def update(id: Long, newDescription: ProductDescription): Future[Int] = {
    val userUpdate: ProductDescription = newDescription.copy(id)
    db.run(descriptionTab.filter(_.id === id).update(userUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(descriptionTab.filter(_.id === id).delete)
}
