package repositories

import models.Payment
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import java.time.Instant
import java.util.Date
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, val userRepository: UserRepository, val creditCardRepository: CreditCardRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class PaymentTable(tag: Tag) extends Table[Payment](tag, "payment") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def userId = column[Long]("user_id")
    def user_fk = foreignKey("user_fk", userId, user_)(_.id)
    def creditCardId = column[Long]("credit_card_id")
    def creditCard_fk = foreignKey("credit_card_id_fk", creditCardId, creditCard_)(_.id)
    def amount = column[Int]("amount")

    def * = (id, userId, creditCardId, amount) <> ((Payment.apply _).tupled, Payment.unapply)
  }

  import creditCardRepository.CreditCardTable
  import userRepository.UserTable

  val payment = TableQuery[PaymentTable]
  val user_ = TableQuery[UserTable]
  val creditCard_ = TableQuery[CreditCardTable]

  def list(): Future[Seq[Payment]] = db.run {
    payment.result
  }

  def listByUserId(userId: Long): Future[Seq[Payment]] = db.run {
    payment.filter(_.userId === userId).result
  }

  def create(userId: Long, creditCardId: Long, amount: Int): Future[Payment] = db.run {
    (payment.map(pmt => (pmt.userId, pmt.creditCardId, pmt.amount))
      returning payment.map(_.id)
      into { case ((userId, creditCardId, amount), id) => Payment(id, userId, creditCardId, amount) }
      ) += (userId, creditCardId, amount)
  }

  def getByIdOption(id: Long): Future[Option[Payment]] = db.run {
    payment.filter(_.id === id).result.headOption
  }

  def update(id: Long, newPayment: Payment): Future[Int] = {
    val paymentToUpdate: Payment = newPayment.copy(id)
    db.run(payment.filter(_.id === id).update(paymentToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(payment.filter(_.id === id).delete)
}
