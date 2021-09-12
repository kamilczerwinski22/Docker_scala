package services

import javax.inject.{Inject, Singleton}
import models.Basket
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class BasketRepository @Inject()(dbConfigProvider: DatabaseConfigProvider,
                                 val userRepository: UserRepository,
                                 val paymentRepository: PaymentRepository,
                                 val couponRepository: CouponRepository)(implicit ec: ExecutionContext){



  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  import userRepository.UserTable
  import paymentRepository.PaymentTable
  import couponRepository.CouponTable
  val user = TableQuery[UserTable]
  val payment = TableQuery[PaymentTable]
  val coupon = TableQuery[CouponTable]


  class BasketTable(tag: Tag) extends Table[Basket](tag, "basket") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("user_id")
    def userFk = foreignKey("user_fk", userId, user)(_.id)

    def paymentId = column[Long]("payment_id")
    def paymentFk = foreignKey("payment_id_fk", paymentId, payment)(_.id)

    def couponId = column[Long]("coupon_id", O.Default(0))
    def couponFk = foreignKey("coupon_id_fk", couponId, coupon)(_.id)

    def * = (id, userId, paymentId, couponId) <> ((Basket.apply _).tupled, Basket.unapply)
  }


  val basket = TableQuery[BasketTable]


  def create(userId: Long, paymentId: Long, couponId: Long): Future[Basket] = db.run {
    (basket.map(bst => (bst.userId, bst.paymentId, bst.couponId))
      returning basket.map(_.id)
      into {case ((userId, paymentId, voucherId), id) => Basket(id, userId, paymentId, voucherId)}
      ) += (userId, paymentId, couponId)
  }

  def getById(id: Long): Future[Basket] = db.run {
    basket.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Basket]] = db.run {
    basket.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[Basket]] = db.run {
    basket.result
  }

  def listByIds(ids: Seq[Long]): Future[Seq[Basket]] = db.run {
    basket.filter(_.id.inSet(ids)).result
  }

  def listByUserId(userId: Long): Future[Seq[Basket]] = db.run {
    basket.filter(_.userId === userId).result
  }

  def listByPaymentId(paymentId: Long): Future[Seq[Basket]] = db.run {
    basket.filter(_.paymentId === paymentId).result
  }

  def listByCouponId(couponId: Long): Future[Seq[Basket]] = db.run {
    basket.filter(_.couponId === couponId).result
  }

  def update(id: Long, newBasket: Basket): Future[Int] = {
    val basketToUpdate: Basket = newBasket.copy(id)
    db.run(basket.filter(_.id === id).update(basketToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(basket.filter(_.id === id).delete)
}
