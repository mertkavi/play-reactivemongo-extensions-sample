package models

import controllers.Application._
import play.api.Play.current
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.BSONFormats._
import reactivemongo.bson.BSONObjectID
import reactivemongo.extensions.json.dao.JsonDao

/**
 * Keeps the mail data.
 *
 * @param _id Auto-generated [[reactivemongo.bson.BSONObjectID]] for the [[Mail]] object.
 * @param contact Contact attribute for the [[Mail]] object.
 * @param folder Folder attribute for the [[Mail]] object.
 * @param subject Subject attribute for the [[Mail]] object.
 * @param message Message attribute for the [[Mail]] object.
 */
case class Mail(_id : BSONObjectID = BSONObjectID.generate, contact: String,
                 folder: String, subject: String, message: String)

/**
 *
 * Mail object for define data format for the ReactiveMongo Extensions.
 */
object Mail {
  implicit val mailFormat = Json.format[Mail]
}

/**
 * Dao implementation for the for the [[Mail]] objects.
 * This object defines specific CRUD functions.
 */
object MailDao extends JsonDao[Mail, BSONObjectID](() => ReactiveMongoPlugin.db, "mails") {

  /**
   * Lists mails.
   * Fetchs from database according to folder name.
   *
   * @param folder Folder attribute for for the [[Mail]] object.
   * @return [[scala.concurrent.Future]] as a [[List]]
   */
  def listMails(folder: String) = {
    MailDao.findAll(selector = "folder" $eq folder, sort = "_id" $eq 1)
  }

  /**
   * Create mail.
   * Insert a [[Mail]] object.
   *
   * @param mail Inserted [[Mail]] object.
   * @return [[scala.concurrent.Future]] as a [[reactivemongo.core.commands.LastError]]
   */
  def createMail(mail: Mail) = {
    MailDao.insert(mail)
  }

  /**
   * Update the [[Mail]] object according to folder name and id.
   *
   * @param folder Folder attribute for for the [[Mail]] object.
   * @param id [[reactivemongo.bson.BSONObjectID]] for the [[Mail]] object.
   * @return [[scala.concurrent.Future]] as a [[reactivemongo.core.commands.LastError]]
   */
  def updateMail(folder: String, id: BSONObjectID) = {
    MailDao.updateById(id, $set("folder" -> folder))
  }

  /**
   * Delete the [[Mail]] object from the database according to id.
   * @param id [[reactivemongo.bson.BSONObjectID]] for the [[Mail]] object.
   * @return [[scala.concurrent.Future]] as a [[reactivemongo.core.commands.LastError]]
   */
  def deleteMail(id: BSONObjectID) = {
    MailDao.removeById(id)
  }
}