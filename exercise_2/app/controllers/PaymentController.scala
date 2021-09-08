package controllers

import akka.actor.ActorSystem
import javax.inject.Inject
import models.SampleMethod
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration._

class PaymentController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  def sampleMethod(delayTime: FiniteDuration, name: String = "sample_name"): Future[SampleMethod] = {
    val sample = SampleMethod("sample_id", name)
    val promise: Promise[SampleMethod] = Promise[SampleMethod]()
    actorSystem.scheduler.scheduleOnce(delayTime) {
      promise.success(sample)
    } (actorSystem.dispatcher)
    promise.future
  }


  def createPayment(): Action[AnyContent] = Action.async { implicit request =>
    println("Payment created")
    sampleMethod(20.millisecond, request.body.asJson.get("name").as[String]).map { msg => Ok(Json.toJson(msg))}
  }

  def getPayment(id: String): Action[AnyContent] = Action.async {
    println("Get Payment with id: ", id)
    sampleMethod(20.millisecond).map { msg => Ok(Json.toJson(msg))}
  }

  def updatePayment(): Action[AnyContent] = Action.async { implicit request =>
    println("Payment updated")
    sampleMethod(20.millisecond, request.body.asJson.get("name").as[String]).map { msg => Ok(Json.toJson(msg))}
  }

  def deletePayment(id: String): Action[AnyContent] = Action.async {
    println("Payment :", id, " deleted")
    sampleMethod(20.millisecond).map { msg => Ok(Json.toJson(msg))}
  }
}
