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
}
