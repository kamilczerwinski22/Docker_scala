package services

import models.CreditCard
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import java.time.Instant
import java.util.Date
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreditCardRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, val userRepository: UserRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CreditCardTable(tag: Tag) extends Table[CreditCard](tag, "credit_card") {
    def currentWhenInserting = new Timestamp((new Date).getTime)

    def id= column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("user_id")

    def user_fk = foreignKey("user_fk", userId, user_)(_.id)

    def cardholderName = column[String]("cardholder_name")

    def number = column[String]("number")

    def expDate = column[String]("exp_date")

    def cvcCode = column[String]("cvc_code")

    def * = (id, userId, cardholderName, number, expDate, cvcCode) <> ((CreditCard.apply _).tupled, CreditCard.unapply)
  }

  import userRepository.UserTable

  val creditCard = TableQuery[CreditCardTable]
  val user_ = TableQuery[UserTable]

  def create(userId: Long, cardholderName: String, number: String, expDate: String, cvcCode: String): Future[CreditCard] = db.run {
    (creditCard.map(c => (c.userId, c.cardholderName, c.number, c.expDate, c.cvcCode))
      returning creditCard.map(_.id)
      into { case ((userId, cardholderName, number, expDate, cvcCode), id) => CreditCard(id, userId, cardholderName, number, expDate, cvcCode) }
      ) += (userId, cardholderName, number, expDate, cvcCode)
  }

  def getByIdOption(id: Long): Future[Option[CreditCard]] = db.run {
    creditCard.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[CreditCard]] = db.run {
    creditCard.result
  }

  def listByUserId(userId: Long): Future[Seq[CreditCard]] = db.run {
    creditCard.filter(_.userId === userId).result
  }

  def update(id: Long, newCreditCard: CreditCard): Future[Int] = {
    val creditCardToUpdate: CreditCard = newCreditCard.copy(id)
    db.run(creditCard.filter(_.id === id).update(creditCardToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(creditCard.filter(_.id === id).delete)
}
