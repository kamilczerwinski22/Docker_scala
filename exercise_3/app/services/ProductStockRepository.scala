package services

import javax.inject.{Inject, Singleton}
import models.ProductStock
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ProductStockRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext){

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class ProductStockTable(tag: Tag) extends Table[ProductStock](tag, "stock") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def unitPrice = column[Int]("unit_price")
    def totalPrice = column[Int]("total_price")
    def totalStock = column[Int]("total_stock")

    def * = (id, unitPrice, totalPrice, totalStock) <> ((ProductStock.apply _).tupled, ProductStock.unapply)
  }

  val stock = TableQuery[ProductStockTable]

  def create(unitPrice: Int, totalPrice: Int, totalStock: Int): Future[ProductStock] = db.run {
    (stock.map(st => (st.unitPrice, st.totalPrice, st.totalStock))
      returning stock.map(_.id)
      into {case ((unitPrice, totalPrice, totalStock),id) => ProductStock(id, unitPrice, totalPrice, totalStock)}
      ) += (unitPrice, totalPrice, totalStock)
  }

  def getById(id: Long): Future[ProductStock] = db.run {
    stock.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[ProductStock]] = db.run {
    stock.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[ProductStock]] = db.run {
    stock.result
  }

  def update(id: Long, newProductStock: ProductStock): Future[Int] = {
    val stockToUpdate: ProductStock = newProductStock.copy(id)
    db.run(stock.filter(_.id === id).update(stockToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(stock.filter(_.id === id).delete)

}
