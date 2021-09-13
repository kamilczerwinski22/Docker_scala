package services

import javax.inject.{Inject, Singleton}
import models.{Order, OrderItem, Product}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.duration.DurationInt

import scala.concurrent.{Await, ExecutionContext, Future}


@Singleton
class OrderItemRepository @Inject() (val dbConfigProvider: DatabaseConfigProvider,
                                     val orderRepository: OrderRepository,
                                     val productRepository: ProductRepository,
                                     val userRepository: UserRepository)(implicit ec: ExecutionContext){

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class OrderItemTable(tag: Tag) extends Table[OrderItem](tag, "order_item") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def orderId = column[Long]("order_id")

    def order_fk = foreignKey("order_fk", orderId, order_)(_.id)

    def productId = column[Long]("product_id")

    def product_fk = foreignKey("product_id_fk", productId, product)(_.id)

    def amount = column[Int]("amount")

    def * = (id, orderId, productId, amount) <> ((OrderItem.apply _).tupled, OrderItem.unapply)
  }

  import orderRepository.OrderTable
  import productRepository.ProductTable

  val orderItem = TableQuery[OrderItemTable]
  val order_ = TableQuery[OrderTable]
  val product = TableQuery[ProductTable]

  def create(orderId: Long, productId: Long, amount: Int): Future[OrderItem] = db.run {
    (orderItem.map(p => (p.productId, p.orderId, p.amount))
      returning orderItem.map(_.id)
      into { case ((productId, orderId, amount), id) => OrderItem(id, productId, orderId, amount) }
      ) += (productId, orderId, amount)
  }

  def getByIdOption(id: Long): Future[Option[OrderItem]] = db.run {
    orderItem.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[OrderItem]] = db.run {
    orderItem.result
  }

  def listByOrderId(orderId: Long): Future[Seq[OrderItem]] = db.run {
    orderItem.filter(_.orderId === orderId).result
  }

  def listByProductId(productId: Long): Future[Seq[OrderItem]] = db.run {
    orderItem.filter(_.productId === productId).result
  }

  def listOrdersByProductId(productId: Long): Future[Seq[Order]] = {
    val orderProducts = Await.result(listByProductId(productId), 10.second)
    val ids = orderProducts.map { orderProduct => orderProduct.orderId }
    orderRepository.listByIds(ids)
  }

  def listProductsByOrderId(orderId: Long): Future[Seq[Product]] = {
    val orderProducts = Await.result(listByOrderId(orderId), 10.second)
    val ids = orderProducts.map { orderProduct => orderProduct.orderId }
    productRepository.listByIds(ids)
  }

  def update(id: Long, newOrderItem: OrderItem): Future[Int] = {
    val orderProductToUpdate: OrderItem = newOrderItem.copy(id)
    db.run(orderItem.filter(_.id === id).update(orderProductToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(orderItem.filter(_.id === id).delete)

}
