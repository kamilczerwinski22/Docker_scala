package services


import models.User
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  class UserTable(tag: Tag) extends Table[User](tag, "user") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def email = column[String]("email")
    def password = column[String]("password")
    def nickname = column[String]("nickname")

    def * = (id, email, password, nickname) <> ((User.apply _).tupled, User.unapply)
  }

  val user = TableQuery[UserTable]

  def list(): Future[Seq[User]] = db.run {
    user.result
  }

  def create(email: String, password: String, nickname: String) : Future[User] = db.run {
    (user.map(us => (us.email, us.nickname, us.password))
      returning user.map(_.id)
      into {case ((email, password, nickname), id) => User(id, email, password, nickname )}
      ) += (email, password, nickname)
  }

  def getById(id: Long): Future[User] = db.run {
    user.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[User]] = db.run {
    user.filter(_.id === id).result.headOption
  }

  def update(id: Long, newUser: User): Future[Int] = {
    val userUpdate: User = newUser.copy(id)
    db.run(user.filter(_.id === id).update(userUpdate))
  }

  def delete(id: Long): Future[Int] = db.run(user.filter(_.id === id).delete)

}
