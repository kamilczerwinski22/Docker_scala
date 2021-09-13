package controllers.api

import akka.actor.ActorSystem
import javax.inject.Inject
import models.Coupon
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.CouponRepository

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}

class CouponController @Inject()(val couponRepo: CouponRepository, cc: ControllerComponents, actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends AbstractController(cc) {


  def getCouponById(id: Long): Action[AnyContent] = Action.async {
    val coupon = couponRepo.getByIdOption(id)
    coupon.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound(" coupon cannot be found")
    }
  }

  def listCoupons(): Action[AnyContent] = Action.async {

    val coupons = couponRepo.list()
    coupons.map { coupons =>
      Ok(Json.toJson(coupons))
    }
  }

  def createCoupon(): Action[JsValue] = Action.async(parse.json) { implicit request =>

    request.body.validate[Coupon].map {
      coupon =>
        couponRepo.create(coupon.amount, coupon.usages).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("incorrect data rovided")))
  }

  def updateCoupon(): Action[JsValue] = Action.async(parse.json) { request =>

    request.body.validate[Coupon].map {
      coupon =>
        couponRepo.update(coupon.id, coupon).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteCoupon(id: Long): Action[AnyContent] = Action.async {
    couponRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
