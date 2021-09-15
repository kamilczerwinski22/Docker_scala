package controllers.api

import models.User
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.UserRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(val userRepository: UserRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {


  def getUserById(id: Long): Action[AnyContent] = Action.async {

    val user = userRepository.getByIdOption(id)
    user.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("user with given id cannot be found")
    }
  }

  def listUsers(): Action[AnyContent] = Action.async {

    val users = userRepository.list()
    users.map { users =>
      Ok(Json.toJson(users))
    }
  }

  def createUser(): Action[JsValue] = Action.async(parse.json) { implicit request =>

    request.body.validate[User].map {
      user =>
        userRepository.create(user.email, user.nickname, user.password).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("incorrect data have been provided")))
  }

  def updateUser(): Action[JsValue] = Action.async(parse.json) { request =>

    request.body.validate[User].map {
      user =>
        userRepository.update(user.id, user).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteUser(id: Long): Action[AnyContent] = Action.async {

    userRepository.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
