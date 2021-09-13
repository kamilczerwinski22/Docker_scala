package controllers.view

import models.Coupon
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.CouponRepository
import javax.inject._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CouponController @Inject()(couponRepo: CouponRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def listCoupons: Action[AnyContent] = Action.async { implicit request =>
    couponRepo.list().map(coupons => Ok(views.html.coupon_list(coupons)))
  }

  def createCoupon(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val coupons = couponRepo.list()
    coupons.map(_ => Ok(views.html.coupon_create(couponForm)))
  }

  def createCouponHandle(): Action[AnyContent] = Action.async { implicit request =>
    couponForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.coupon_create(errorForm))
        )
      },
      coupon => {
        couponRepo.create(coupon.amount, coupon.usages).map { _ =>
          Redirect("/form/coupon/list")
        }
      }
    )
  }

  def updateCoupon(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val coupon = couponRepo.getByIdOption(id)
    coupon.map(coupon => {
      val prodForm = updateCouponForm.fill(UpdateCouponForm(coupon.get.id, coupon.get.amount, coupon.get.usages))
      Ok(views.html.coupon_update(prodForm))
    })
  }

  def updateCouponHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateCouponForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.coupon_update(errorForm))
        )
      },
      coupon => {
        couponRepo.update(coupon.id, Coupon(coupon.id, coupon.amount, coupon.usages)).map { _ =>
          Redirect("/form/coupon/list")
        }
      }
    )
  }

  def deleteCoupon(id: Long): Action[AnyContent] = Action {
    couponRepo.delete(id)
    Redirect("/form/coupon/list")
  }

  // utilities

  val couponForm: Form[CreateCouponForm] = Form {
    mapping(
      "amount" -> number,
      "usages" -> number,
    )(CreateCouponForm.apply)(CreateCouponForm.unapply)
  }

  val updateCouponForm: Form[UpdateCouponForm] = Form {
    mapping(
      "id" -> longNumber,
      "amount" -> number,
      "usages" -> number,
    )(UpdateCouponForm.apply)(UpdateCouponForm.unapply)
  }
}

case class CreateCouponForm(amount: Int, usages: Int)

case class UpdateCouponForm(id: Long, amount: Int, usages: Int)