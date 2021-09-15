package services

import models.Stock
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import java.time.Instant
import java.util.Date
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StockRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class StockTable(tag: Tag) extends Table[Stock](tag, "stock") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def unitPrice = column[Int]("unit_price")
    def totalPrice = column[Int]("total_price")
    def totalStock = column[Int]("total_stock")

    def * = (id, unitPrice, totalPrice, totalStock) <> ((Stock.apply _).tupled, Stock.unapply)
  }

  val stock = TableQuery[StockTable]

  def create(unitPrice: Int, totalPrice: Int, totalStock: Int): Future[Stock] = db.run {
    (stock.map(s => (s.unitPrice, s.totalPrice, s.totalStock))
      returning stock.map(_.id)
      into { case ((unitPrice, totalPrice, totalStock), id) => Stock(id, unitPrice, totalPrice, totalStock) }
      ) += (unitPrice, totalPrice, totalStock)
  }

  def getByIdOption(id: Long): Future[Option[Stock]] = db.run {
    stock.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[Stock]] = db.run {
    stock.result
  }

  def update(id: Long, newStock: Stock): Future[Int] = {
    val stockToUpdate: Stock = newStock.copy(id)
    db.run(stock.filter(_.id === id).update(stockToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(stock.filter(_.id === id).delete)
}
