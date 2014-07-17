package controllers

import models.{Mail, MailDao}
import play.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import reactivemongo.bson.BSONObjectID
import reactivemongo.extensions.json.dsl.JsonDsl
import models.MailFormats._
import scala.concurrent.Future

/**
 * Singleton controller class for Play. This object provides the handling functions
 * for HTTP requests.
 */
object Application extends Controller with JsonDsl {

  /**
   * Render index template.
   *
   * @return
   */
  def index = Action {
    Ok(views.html.index())
  }

  /**
   * Lists all mails.
   *
   * @param folder Folder attribute of mails.
   * @return A Ok [[play.api.mvc.Result]]
   */
  def listMails(folder: String) = Action.async {
    MailDao.listMails(folder).map {
      case Nil => Ok(Json.toJson(""))
      case mails => Ok(Json.toJson(mails))
    }
  }

  /**
   * Creates and persists mail with coming HTTP request data.
   *
   * @return A Ok [[play.api.mvc.Result]] or InternalServerError [[play.api.mvc.Results.Status]]
   */
  def composeMail = Action.async(BodyParsers.parse.json) { implicit request =>
    val mailResult = request.body.validate[Mail]
    mailResult.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("status" ->"NOT OK", "message" -> JsError.toFlatJson(errors))))
      },
      mail => {
        MailDao.createMail(mail).map(
          _ => Ok(Json.obj("status" ->"OK", "message" -> s"Mail ${mail.subject} sent."))).recover {
          case t: Throwable =>
            Logger.error("CREATE ERROR", t)
            InternalServerError("Unknown error (CREATE).")
        }
      }
    )
  }

  /**
   * Updates folder of mail.
   *
   * @param folder Folder will be updated.
   * @param id BSONObject will be updated.
   * @return A Ok [[play.api.mvc.Result]] or InternalServerError [[play.api.mvc.Results.Status]]
   */
  def updateMail(folder: String, id: BSONObjectID) = Action.async {
    MailDao.updateMail(folder, id).map(_ => Ok(Json.obj("status" ->"OK", "message" -> "Mail updated"))).recover {
      case t: Throwable =>
        Logger.error("UPDATE ERROR", t)
        InternalServerError("Unknown error (UPDATE).")
    }
  }

  /**
   * Deletes a mail from database.
   *
   * @param id BSONObject will deleted.
   * @return A Ok [[play.api.mvc.Result]] or InternalServerError [[play.api.mvc.Results.Status]]
   */
  def deleteMail(id: BSONObjectID) = Action.async {
    MailDao.deleteMail(id).map(_ => Ok(Json.obj("status" ->"OK", "message" -> "Mail deleted"))).recover {
      case t: Throwable =>
        Logger.error("DELETE ERROR", t)
        InternalServerError("Unknown error (DELETE).")
    }
  }
}