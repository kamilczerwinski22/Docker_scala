package services


import javax.inject.{Inject, Singleton}
import models.Payment
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, val userRepository: UserRepository)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  import userRepository.UserTable
  val user = TableQuery[UserTable]

  class PaymentTable(tag: Tag) extends Table[Payment](tag, "payment") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("user_id")
    def userFk = foreignKey("user_fk", userId, user)(_.id)

    def chargeAmount = column[Int]("amount")

    def * = (id, userId, chargeAmount) <> ((Payment.apply _).tupled, Payment.unapply)
  }

  val payment = TableQuery[PaymentTable]

  def list(): Future[Seq[Payment]] = db.run {
    payment.result
  }

  def create(userId: Long, chargeAmount: Int) : Future[Payment] = db.run {
    (payment.map(pm => (pm.userId, pm.chargeAmount))
      returning payment.map(_.id)
      into {case ((userId, chargeAmount), id) => Payment(id, userId, chargeAmount)}
      ) += (userId, chargeAmount)
  }

  def getById(id: Long): Future[Payment] = db.run {
    payment.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Payment]] = db.run {
    payment.filter(_.id === id).result.headOption
  }

  def update(id: Long, newPayment: Payment): Future[Int] = {
    val paymentUpdate: Payment = newPayment.copy(id)
    db.run(payment.filter(_.id === id).update(paymentUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(payment.filter(_.id === id).delete)

}
