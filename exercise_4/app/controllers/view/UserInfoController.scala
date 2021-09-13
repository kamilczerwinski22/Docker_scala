package controllers.view

import models.{User, UserInfo}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.{UserInfoRepository, UserRepository}
import javax.inject._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class UserInfoController @Inject()(userInfoRepo: UserInfoRepository,
                                   userRepo: UserRepository,
                                   cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  var userList: Seq[User] = Seq[User]()
  fetchData()


  def listUserInfos: Action[AnyContent] = Action.async { implicit request =>
    userInfoRepo.list().map(userInfo => Ok(views.html.user_info_list(userInfo)))
  }

  def createUserInfo(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = userRepo.list()

    users.map(users => Ok(views.html.user_info_create(userInfoForm, users)))
  }

  def createUserInfoHandle(): Action[AnyContent] = Action.async { implicit request =>
    userInfoForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.user_info_create(errorForm, userList))
        )
      },
      userInfo => {
        userInfoRepo.create(userInfo.userId, userInfo.firstname, userInfo.lastname, userInfo.address, userInfo.zipcode, userInfo.city, userInfo.country).map { _ =>
          Redirect("/form/user-info/list")
        }
      }
    )
  }

  def updateUserInfo(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val userInfo = userInfoRepo.getByIdOption(id)
    userInfo.map(userInfo => {
      val prodForm = updateUserInfoForm.fill(UpdateUserInfoForm(userInfo.get.id, userInfo.get.userId, userInfo.get.firstname, userInfo.get.lastname, userInfo.get.address, userInfo.get.zipcode, userInfo.get.city, userInfo.get.country))
      Ok(views.html.user_info_update(prodForm, userList))
    })
  }

  def updateUserInfoHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateUserInfoForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.user_info_update(errorForm, userList))
        )
      },
      userInfo => {
        userInfoRepo.update(userInfo.id, UserInfo(userInfo.id, userInfo.userId, userInfo.firstname, userInfo.lastname, userInfo.address, userInfo.zipcode, userInfo.city, userInfo.country)).map { _ =>
          Redirect("/form/user-info/list")
        }
      }
    )
  }

  def deleteUserInfo(id: Long): Action[AnyContent] = Action {
    userInfoRepo.delete(id)
    Redirect("/form/user-info/list")
  }

  val userInfoForm: Form[CreateUserInfoForm] = Form {
    mapping(
      "userId" -> longNumber,
      "firstname" -> nonEmptyText,
      "lastname" -> nonEmptyText,
      "address" -> nonEmptyText,
      "zipcode" -> nonEmptyText,
      "city" -> nonEmptyText,
      "country" -> nonEmptyText,
    )(CreateUserInfoForm.apply)(CreateUserInfoForm.unapply)
  }

  val updateUserInfoForm: Form[UpdateUserInfoForm] = Form {
    mapping(
      "id" -> longNumber,
      "userId" -> longNumber,
      "firstname" -> nonEmptyText,
      "lastname" -> nonEmptyText,
      "address" -> nonEmptyText,
      "zipcode" -> nonEmptyText,
      "city" -> nonEmptyText,
      "country" -> nonEmptyText,
    )(UpdateUserInfoForm.apply)(UpdateUserInfoForm.unapply)
  }

  def fetchData(): Unit = {
    userRepo.list().onComplete {
      case Success(user) => userList = user
      case Failure(e) => print("error while listing users", e)
    }
  }
}

case class CreateUserInfoForm(userId: Long, firstname: String, lastname: String, address: String, zipcode: String, city: String, country: String)

case class UpdateUserInfoForm(id: Long, userId: Long, firstname: String, lastname: String, address: String, zipcode: String, city: String, country: String)