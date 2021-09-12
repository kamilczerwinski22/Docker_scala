package services

import javax.inject.{Inject, Singleton}
import models.Coupon
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class CouponRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext){
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CouponTable(tag: Tag) extends Table[Coupon](tag, "coupon") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def amount = column[Int]("amount")
    def usages = column[Int]("usages")

    def * = (id, amount, usages) <> ((Coupon.apply _).tupled, Coupon.unapply)
  }

  val coupon = TableQuery[CouponTable]

  def create(amount: Int, usages: Int): Future[Coupon] = db.run {
    (coupon.map(c => (c.amount, c.usages))
      returning coupon.map(_.id)
      into {case ((amount, usages),id) => Coupon(id, amount, usages)}
      ) += (amount, usages)
  }

  def getById(id: Long): Future[Coupon] = db.run {
    coupon.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Coupon]] = db.run {
    coupon.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[Coupon]] = db.run {
    coupon.result
  }

  def update(id: Long, newCoupon: Coupon): Future[Int] = {
    val couponUpdate: Coupon = newCoupon.copy(id)
    db.run(coupon.filter(_.id === id).update(couponUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(coupon.filter(_.id === id).delete)
}
