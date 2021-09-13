package services

import models.Payment
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import java.time.Instant
import java.util.Date
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentRepository @Inject()(dbConfigProvider: DatabaseConfigProvider,
                                  val userRepository: UserRepository,
                                  val paymentMoneyRepository: PaymentMoneyRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class PaymentTable(tag: Tag) extends Table[Payment](tag, "payment") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("user_id")

    def user_fk = foreignKey("user_fk", userId, user)(_.id)

    def paymentMoneyId = column[Long]("payment_money_id")

    def paymentMoney_fk = foreignKey("payment_money_fk", paymentMoneyId, paymentMoney)(_.id)

    def amount = column[Int]("amount")

    def * = (id, userId, paymentMoneyId, amount) <> ((Payment.apply _).tupled, Payment.unapply)
  }

  import paymentMoneyRepository.PaymentMoneyTable
  import userRepository.UserTable

  val payment = TableQuery[PaymentTable]
  val user = TableQuery[UserTable]
  val paymentMoney = TableQuery[PaymentMoneyTable]

  def create(userId: Long, paymentMoneyId: Long, amount: Int): Future[Payment] = db.run {
    (payment.map(c => (c.userId, c.paymentMoneyId, c.amount))
      returning payment.map(_.id)
      into { case ((userId, creditCardId, amount), id) => Payment(id, userId, creditCardId, amount) }
      ) += (userId, paymentMoneyId, amount)
  }

  def getByIdOption(id: Long): Future[Option[Payment]] = db.run {
    payment.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[Payment]] = db.run {
    payment.result
  }

  def listByUserId(userId: Long): Future[Seq[Payment]] = db.run {
    payment.filter(_.userId === userId).result
  }

  def listByPaymentMoneyId(paymentMoneyId: Long): Future[Seq[Payment]] = db.run {
    payment.filter(_.paymentMoneyId === paymentMoneyId).result
  }

  def update(id: Long, new_payment: Payment): Future[Int] = {
    val paymentToUpdate: Payment = new_payment.copy(id)
    db.run(payment.filter(_.id === id).update(paymentToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(payment.filter(_.id === id).delete)
}