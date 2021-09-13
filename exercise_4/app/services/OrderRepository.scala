package services

import models.Order
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import java.time.Instant
import java.util.Date
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderRepository @Inject()(dbConfigProvider: DatabaseConfigProvider,
                                val userRepository: UserRepository,
                                val paymentRepository: PaymentRepository,
                                val couponRepository: CouponRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  import paymentRepository.PaymentTable
  import userRepository.UserTable
  import couponRepository.CouponTable
  val user = TableQuery[UserTable]
  val payment = TableQuery[PaymentTable]
  val coupon = TableQuery[CouponTable]

  class OrderTable(tag: Tag) extends Table[Order](tag, "order_") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("user_id")

    def user_fk = foreignKey("user_fk", userId, user)(_.id)

    def paymentId = column[Long]("payment_id")

    def payment_fk = foreignKey("payment_id_fk", paymentId, payment)(_.id)

    def couponId = column[Long]("coupon_id", O.Default(0))

    def coupon_fk = foreignKey("coupon_id_id_fk", couponId, coupon)(_.id)


    def * = (id, userId, paymentId, couponId) <> ((Order.apply _).tupled, Order.unapply)
  }



  val order = TableQuery[OrderTable]

  def create(userId: Long, paymentId: Long, couponId: Long): Future[Order] = db.run {
    (order.map(o => (o.userId, o.paymentId, o.couponId))
      returning order.map(_.id)
      into { case ((userId, paymentId, couponId), id) => Order(id, userId, paymentId, couponId) }
      ) += (userId, paymentId, couponId)
  }

  def getByIdOption(id: Long): Future[Option[Order]] = db.run {
    order.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[Order]] = db.run {
    order.result
  }

  def listByIds(ids: Seq[Long]): Future[Seq[Order]] = db.run {
    order.filter(_.id.inSet(ids)).result
  }

  def listByUserId(userId: Long): Future[Seq[Order]] = db.run {
    order.filter(_.userId === userId).result
  }

  def listByPaymentId(paymentId: Long): Future[Seq[Order]] = db.run {
    order.filter(_.paymentId === paymentId).result
  }

  def listByVoucherId(voucherId: Long): Future[Seq[Order]] = db.run {
    order.filter(_.couponId === voucherId).result
  }

  def update(id: Long, newOrder: Order): Future[Int] = {
    val orderToUpdate: Order = newOrder.copy(id)
    db.run(order.filter(_.id === id).update(orderToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(order.filter(_.id === id).delete)
}
