package services

import javax.inject.{Inject, Singleton}
import models.{Basket, BasketItem, Product}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}


@Singleton
class BasketItemRepository @Inject()(dbConfigProvider: DatabaseConfigProvider,
                                     val basketRepository: BasketRepository,
                                     val productRepository: ProductRepository)(implicit ec: ExecutionContext){


  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  import basketRepository.BasketTable
  import productRepository.ProductTable
  val basket = TableQuery[BasketTable]
  val product = TableQuery[ProductTable]

  class BasketItemTable(tag: Tag) extends Table[BasketItem](tag, "basket_item") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def orderId = column[Long]("order_id")
    def orderFk = foreignKey("order_fk", orderId, basket)(_.id)

    def productId = column[Long]("product_id")
    def productFk = foreignKey("product_id_fk", productId, product)(_.id)

    def amount = column[Int]("amount")

    def * = (id, orderId, productId, amount) <> ((BasketItem.apply _).tupled, BasketItem.unapply)
  }



  val basketItem = TableQuery[BasketItemTable]

  def create(orderId: Long, productId: Long, amount: Int): Future[BasketItem] = db.run {
    (basketItem.map(bi => (bi.orderId, bi.productId, bi.amount))
      returning basketItem.map(_.id)
      into {case ((orderId, productId, amount), id) => BasketItem(id, orderId, productId, amount)}
      ) += (orderId, productId, amount)
  }

  def getById(id: Long): Future[BasketItem] = db.run {
    basketItem.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[BasketItem]] = db.run {
    basketItem.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[BasketItem]] = db.run {
    basketItem.result
  }

  def listByOrderId(orderId: Long): Future[Seq[BasketItem]] = db.run {
    basketItem.filter(_.orderId === orderId).result
  }

  def listByProductId(productId: Long): Future[Seq[BasketItem]] = db.run {
    basketItem.filter(_.productId === productId).result
  }

  def listOrdersByProductId(productId: Long): Future[Seq[Basket]] = {
    val baskedProducts = Await.result(listByProductId(productId), 10.second)
    val ids = baskedProducts.map { baskedProduct => baskedProduct.basketId }
    basketRepository.listByIds(ids)
  }

  def listProductsByOrderId(basketId: Long): Future[Seq[Product]] = {
    val baskedProducts = Await.result(listByOrderId(basketId), 10.second)
    val ids = baskedProducts.map { baskedProduct => baskedProduct.productId }
    productRepository.listByIds(ids)
  }

  def update(id: Long, newBasketItem: BasketItem): Future[Int] = {
    val basketItemUpdate: BasketItem = newBasketItem.copy(id)
    db.run(basketItem.filter(_.id === id).update(basketItemUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(basketItem.filter(_.id === id).delete)
}
