package controllers.api

import models.Voucher
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import repositories.VoucherRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VoucherController @Inject()(val voucherRepo: VoucherRepository, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def listVouchers(): Action[AnyContent] = Action.async {
    val vouchers = voucherRepo.list()
    vouchers.map { vouchers =>
      Ok(Json.toJson(vouchers))
    }
  }

  def getVoucherById(id: Long): Action[AnyContent] = Action.async {
    val voucher = voucherRepo.getByIdOption(id)
    voucher.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("voucher with given id cannot be found")
    }
  }

  def getVoucherByCode(code: String): Action[AnyContent] = Action.async {
    val voucher = voucherRepo.getByCodeOption(code)
    voucher.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("voucher not be found")
    }
  }

  def createVoucher(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Voucher].map {
      voucher =>
        voucherRepo.create(voucher.code, voucher.amount, voucher.usages).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("incorrect data")))
  }

  def updateVoucher(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Voucher].map {
      voucher =>
        voucherRepo.update(voucher.id, voucher).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def deleteVoucher(id: Long): Action[AnyContent] = Action.async {
    voucherRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
