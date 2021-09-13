package services

import models.UserInfo
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import java.time.Instant
import java.util.Date
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserInfoRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, val userRepository: UserRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class UserAddressTable(tag: Tag) extends Table[UserInfo](tag, "user_info") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("user_id")

    def userId_fk = foreignKey("user_fk", userId, user)(_.id)

    def firstname = column[String]("firstname")

    def lastname = column[String]("lastname")

    def address = column[String]("address")

    def zipcode = column[String]("zipcode")

    def city = column[String]("city")

    def country = column[String]("country")

    def * = (id, userId, firstname, lastname, address, zipcode, city, country) <> ((UserInfo.apply _).tupled, UserInfo.unapply)
  }

  import userRepository.UserTable

  val userInfo = TableQuery[UserAddressTable]
  val user = TableQuery[UserTable]

  def create(userId: Long, firstname: String, lastname: String, address: String, zipcode: String, city: String, country: String): Future[UserInfo] = db.run {
    (userInfo.map(a => (a.userId, a.firstname, a.lastname, a.address, a.zipcode, a.city, a.country))
      returning userInfo.map(_.id)
      into { case ((userId, firstname, lastname, address, zipcode, city, country), id) => UserInfo(id, userId, firstname, lastname, address, zipcode, city, country) }
      ) += (userId, firstname, lastname, address, zipcode, city, country)
  }

  def getByIdOption(id: Long): Future[Option[UserInfo]] = db.run {
    userInfo.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[UserInfo]] = db.run {
    userInfo.result
  }

  def listByUserId(userId: Long): Future[Seq[UserInfo]] = db.run {
    userInfo.filter(_.userId === userId).result
  }

  def update(id: Long, newUserInfo: UserInfo): Future[Int] = {
    val userInfoToUpdate: UserInfo = newUserInfo.copy(id)
    db.run(userInfo.filter(_.id === id).update(userInfoToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(userInfo.filter(_.id === id).delete)
}
