package controllers

import javax.inject.{Inject, Singleton}
import models.SampleMethod
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.duration._

import akka.actor.ActorSystem
import scala.concurrent.{ExecutionContext, Future, Promise}



@Singleton
class UserController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends AbstractController(cc) {


  def sampleMethod(delayTime: FiniteDuration, name: String = "sample_name"): Future[SampleMethod] = {
    val sample = SampleMethod("sample_id", name)
    val promise: Promise[SampleMethod] = Promise[SampleMethod]()
    actorSystem.scheduler.scheduleOnce(delayTime) {
      promise.success(sample)
    } (actorSystem.dispatcher)
    promise.future
  }


  def createUser(): Action[AnyContent] = Action.async { implicit request =>
    println("User created")
    sampleMethod(20.millisecond, request.body.asJson.get("name").as[String]).map { msg => Ok(Json.toJson(msg))}
  }

  def getUser(id: String): Action[AnyContent] = Action.async {
    println("Get user with id: ", id)
    sampleMethod(20.millisecond).map { msg => Ok(Json.toJson(msg))}
  }

  def updateUser(): Action[AnyContent] = Action.async { implicit request =>
    println("User updated")
    sampleMethod(20.millisecond, request.body.asJson.get("name").as[String]).map { msg => Ok(Json.toJson(msg))}
  }

  def deleteUser(id: String): Action[AnyContent] = Action.async {
    println("User :", id, " deleted")
    sampleMethod(20.millisecond).map { msg => Ok(Json.toJson(msg))}
  }

  def listUsers(): Action[AnyContent] = Action.async {
    print("List of users")
    sampleMethod(20.millisecond).map { msg => Ok(Json.toJson(msg))}
  }
}
