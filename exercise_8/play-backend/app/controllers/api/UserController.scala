package controllers.api

import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import repositories.UserRepository
import javax.inject._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(val userRepository: UserRepository,
                               cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def listUsers(): Action[AnyContent] = Action.async {
    val users = userRepository.list()
    users.map { users =>
      Ok(Json.toJson(users))
    }
  }

  def getUserById(id: Long): Action[AnyContent] = Action.async {
    val user = userRepository.getByIdOption(id)
    user.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("user not found")
    }
  }

//  def createUser(): Action[JsValue] = Action.async(parse.json) { implicit request =>
//    request.body.validate[User].map {
//      user =>
//        userRepository.create(user.id, user.loginInfo, user.email).map { res =>
//          Ok(Json.toJson(res))
//        }
//    }.getOrElse(Future.successful(BadRequest("incorrect data have been provided")))
//  }
//
//  def updateUser(): Action[JsValue] = Action.async(parse.json) { request =>
//    request.body.validate[User].map {
//      user =>
//        userRepository.update(user.id, user).map { res =>
//          Ok(Json.toJson(res))
//        }
//    }.getOrElse(Future.successful(BadRequest("invalid json")))
//  }
//
//  def deleteUser(id: Long): Action[AnyContent] = Action.async {
//    userRepository.delete(id).map { res =>
//      Ok(Json.toJson(res))
//    }
//  }
}
