package controllers.api

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.duration._
import akka.actor.ActorSystem
import models.User
import services.UserRepository

import scala.concurrent.{ExecutionContext, Future, Promise}



@Singleton
class UserController @Inject()(val userRepo: UserRepository,
                               cc: ControllerComponents, actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends AbstractController(cc) {


  def getUserById(id: Long): Action[AnyContent] = Action.async {

    val user = userRepo.getByIdOption(id)
    user.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("user cannot be found")
    }
  }

  def listUsers(): Action[AnyContent] = Action.async {

    val users = userRepo.list()
    users.map { users =>
      Ok(Json.toJson(users))
    }
  }

  def createUser(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[User].map {
      user =>
        userRepo.create(user.email, user.password, user.nickname).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("incorrect data provided")))
  }


  def updateUser(): Action[JsValue] = Action.async(parse.json) { request =>

    request.body.validate[User].map {
      user =>
        userRepo.update(user.id, user).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteUser(id: Long): Action[AnyContent] = Action.async {
    userRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
