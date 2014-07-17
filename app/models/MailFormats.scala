package models

import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, JsPath, Writes}

object MailFormats {
  implicit def mailWrites: Writes[Mail] = (
    (JsPath \ "id").write[String] and
    (JsPath \ "contact").write[String] and
    (JsPath \ "subject").write[String] and
    (JsPath \ "message").write[String] and
    (JsPath \ "folder").write[String]
    )(mail => (mail._id.stringify, mail.contact,
    mail.subject, mail.message, mail.folder))

  implicit def mailListWrites: Writes[List[Mail]] = Writes.list(mailWrites)

  implicit def mailReads: Reads[Mail] = (
    (JsPath \ "contact").read[String] and
    (JsPath \ "subject").read[String] and
    (JsPath \ "message").read[String] and
    (JsPath \ "folder").read[String]
    )((contact, subject, message, folder) => Mail(contact = contact,
    subject = subject, message = message, folder = folder))
}