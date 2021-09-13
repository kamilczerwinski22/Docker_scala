package controllers.api

import javax.inject.{Inject, Singleton}
import models.UserInfo
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.UserInfoRepository

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class UserInfoController @Inject()(val userInfoRepo: UserInfoRepository,
                                   cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc){

  def getUserInfoById(id: Long): Action[AnyContent] = Action.async {
    val userInfo = userInfoRepo.getByIdOption(id)
    userInfo.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("userinfo cannot be found")
    }
  }

  def listUserInfos(): Action[AnyContent] = Action.async {

    val userInfos = userInfoRepo.list()
    userInfos.map { userAddresses =>
      Ok(Json.toJson(userAddresses))
    }
  }

  def listUserInfosByUserId(userId: Long): Action[AnyContent] = Action.async {
    val userInfos = userInfoRepo.listByUserId(userId)
    userInfos.map { userAddresses =>
      Ok(Json.toJson(userAddresses))
    }
  }

  def createUserInfo(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[UserInfo].map {
      userInfo =>
        userInfoRepo.create(userInfo.userId, userInfo.firstname, userInfo.lastname, userInfo.address, userInfo.zipcode, userInfo.city, userInfo.country).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("incorrect data have been provided")))
  }

  def updateUserInfo(): Action[JsValue] = Action.async(parse.json) { request =>

    request.body.validate[UserInfo].map {
      userInfo =>
        userInfoRepo.update(userInfo.id, userInfo).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteUserInfo(id: Long): Action[AnyContent] = Action.async {
    userInfoRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

}
