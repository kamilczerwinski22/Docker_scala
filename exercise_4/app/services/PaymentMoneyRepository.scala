package services

import models.PaymentMoney
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import java.time.Instant
import java.util.Date
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class PaymentMoneyRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, val userRepository: UserRepository)(implicit ec: ExecutionContext){

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class PaymentMoneyTable(tag: Tag) extends Table[PaymentMoney](tag, "payment_money") {
    def currentWhenInserting = new Timestamp((new Date).getTime)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("user_id")

    def user_fk = foreignKey("user_fk", userId, user)(_.id)

    def cardholderName = column[String]("cardholder_name")

    def * = (id, userId, cardholderName) <> ((PaymentMoney.apply _).tupled, PaymentMoney.unapply)
  }

  import userRepository.UserTable

  val paymentMoney = TableQuery[PaymentMoneyTable]
  val user = TableQuery[UserTable]

  def create(userId: Long, cardholderName: String): Future[PaymentMoney] = db.run {
    (paymentMoney.map(c => (c.userId, c.cardholderName))
      returning paymentMoney.map(_.id)
      into { case ((userId, cardholderName), id) => PaymentMoney(id, userId, cardholderName) }
      ) += (userId, cardholderName)
  }

  def getByIdOption(id: Long): Future[Option[PaymentMoney]] = db.run {
    paymentMoney.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[PaymentMoney]] = db.run {
    paymentMoney.result
  }

  def listByUserId(userId: Long): Future[Seq[PaymentMoney]] = db.run {
    paymentMoney.filter(_.userId === userId).result
  }

  def update(id: Long, newPaymentMoney: PaymentMoney): Future[Int] = {
    val paymentMoneyToUpdate: PaymentMoney = newPaymentMoney.copy(id)
    db.run(paymentMoney.filter(_.id === id).update(paymentMoneyToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(paymentMoney.filter(_.id === id).delete)
}
