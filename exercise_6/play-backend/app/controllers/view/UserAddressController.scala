package controllers.view

import models.{User, UserAddress}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.{UserAddressRepository, UserRepository}

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class UserAddressController @Inject()(userAddressRepo: UserAddressRepository, userRepo: UserRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  var userList: Seq[User] = Seq[User]()

  // fill above lists
  fetchData()

  def listUserAddresses: Action[AnyContent] = Action.async { implicit request =>
    userAddressRepo.list().map(userAddresses => Ok(views.html.user_address_list(userAddresses)))
  }

  def createUserAddress(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = userRepo.list()

    users.map(users => Ok(views.html.user_address_create(userAddressForm, users)))
  }

  def createUserAddressHandle(): Action[AnyContent] = Action.async { implicit request =>
    userAddressForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.user_address_create(errorForm, userList))
        )
      },
      userAddress => {
        userAddressRepo.create(userAddress.userId, userAddress.firstname, userAddress.lastname, userAddress.address, userAddress.zipcode, userAddress.city, userAddress.country).map { _ =>
          Redirect("/form/user-address/list")
        }
      }
    )
  }

  def updateUserAddress(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val userAddress = userAddressRepo.getByIdOption(id)
    userAddress.map(userAddress => {
      val prodForm = updateUserAddressForm.fill(UpdateUserAddressForm(userAddress.get.id, userAddress.get.userId, userAddress.get.firstname, userAddress.get.lastname, userAddress.get.address, userAddress.get.zipcode, userAddress.get.city, userAddress.get.country))
      Ok(views.html.user_address_update(prodForm, userList))
    })
  }

  def updateUserAddressHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateUserAddressForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.user_address_update(errorForm, userList))
        )
      },
      userAddress => {
        userAddressRepo.update(userAddress.id, UserAddress(userAddress.id, userAddress.userId, userAddress.firstname, userAddress.lastname, userAddress.address, userAddress.zipcode, userAddress.city, userAddress.country)).map { _ =>
          Redirect("/form/user-address/list")
        }
      }
    )
  }

  def deleteUserAddress(id: Long): Action[AnyContent] = Action {
    userAddressRepo.delete(id)
    Redirect("/form/user-address/list")
  }

  // utilities

  val userAddressForm: Form[CreateUserAddressForm] = Form {
    mapping(
      "userId" -> longNumber,
      "firstname" -> nonEmptyText,
      "lastname" -> nonEmptyText,
      "address" -> nonEmptyText,
      "zipcode" -> nonEmptyText,
      "city" -> nonEmptyText,
      "country" -> nonEmptyText,
    )(CreateUserAddressForm.apply)(CreateUserAddressForm.unapply)
  }

  val updateUserAddressForm: Form[UpdateUserAddressForm] = Form {
    mapping(
      "id" -> longNumber,
      "userId" -> longNumber,
      "firstname" -> nonEmptyText,
      "lastname" -> nonEmptyText,
      "address" -> nonEmptyText,
      "zipcode" -> nonEmptyText,
      "city" -> nonEmptyText,
      "country" -> nonEmptyText,
    )(UpdateUserAddressForm.apply)(UpdateUserAddressForm.unapply)
  }

  def fetchData(): Unit = {
    userRepo.list().onComplete {
      case Success(user) => userList = user
      case Failure(e) => print("error while listing users", e)
    }
  }
}

case class CreateUserAddressForm(userId: Long, firstname: String, lastname: String, address: String, zipcode: String, city: String, country: String)

case class UpdateUserAddressForm(id: Long, userId: Long, firstname: String, lastname: String, address: String, zipcode: String, city: String, country: String)
