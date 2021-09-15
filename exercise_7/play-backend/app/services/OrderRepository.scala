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
class OrderRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, val userRepository: UserRepository, val addressRepository: UserAddressRepository, val paymentRepository: PaymentRepository, val voucherRepository: VoucherRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  import addressRepository.UserAddressTable
  import paymentRepository.PaymentTable
  import userRepository.UserTable
  import voucherRepository.VoucherTable
  val user = TableQuery[UserTable]
  val address = TableQuery[UserAddressTable]
  val payment = TableQuery[PaymentTable]
  val voucher = TableQuery[VoucherTable]

  class OrderTable(tag: Tag) extends Table[Order](tag, "order_") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("user_id")
    def user_fk = foreignKey("user_fk", userId, user)(_.id)
    def addressId = column[Long]("address_id")
    def paymentId = column[Long]("payment_id")
    def payment_fk = foreignKey("payment_id_fk", paymentId, payment)(_.id)
    def voucherId = column[Long]("voucher_id", O.Default(0))
    def voucher_fk = foreignKey("voucher_id_fk", voucherId, voucher)(_.id)

    def * = (id, userId, addressId, paymentId, voucherId) <> ((Order.apply _).tupled, Order.unapply)
  }


  val order = TableQuery[OrderTable]


  def create(userId: Long, addressId: Long, paymentId: Long, voucherId: Long): Future[Order] = db.run {
    (order.map(o => (o.userId, o.addressId, o.paymentId, o.voucherId))
      returning order.map(_.id)
      into { case ((userId, addressId, paymentId, voucherId), id) => Order(id, userId, addressId, paymentId, voucherId) }
      ) += (userId, addressId, paymentId, voucherId)
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

  def listByAddressId(addressId: Long): Future[Seq[Order]] = db.run {
    order.filter(_.addressId === addressId).result
  }

  def listByPaymentId(paymentId: Long): Future[Seq[Order]] = db.run {
    order.filter(_.paymentId === paymentId).result
  }

  def listByVoucherId(voucherId: Long): Future[Seq[Order]] = db.run {
    order.filter(_.voucherId === voucherId).result
  }

  def update(id: Long, newOrder: Order): Future[Int] = {
    val orderToUpdate: Order = newOrder.copy(id)
    db.run(order.filter(_.id === id).update(orderToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(order.filter(_.id === id).delete)
}
