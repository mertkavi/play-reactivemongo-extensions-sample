import play.api.Play.current
import play.api.libs.json.{JsArray, JsString, Json}
import play.api.libs.ws.WS
import play.api.test._
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.extensions.json.fixtures.JsonFixtures

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object ApplicationSpec extends PlaySpecification {

  sequential

  def initiliazeTestData = Await.result(JsonFixtures(ReactiveMongoPlugin.db).load("mails.conf"), 1 seconds)

  "intiliaze test data" in new WithServer {
    initiliazeTestData
  }

  def checkFolders(folder: String): String = {
    val url = s"http://localhost:9000/mails/${folder}"
    val response = await(WS.url(url).withHeaders(CONTENT_TYPE -> "application/json", ACCEPT -> "application/json").get())
    response.status must equalTo(OK)
    val results = response.json.as[JsArray].value
    results(0) \ "folder" must beEqualTo(JsString(folder))
    results(0) \ "id" must anInstanceOf[JsString]
    results(0) \ "contact" must beAnInstanceOf[JsString]
    results(0) \ "subject" must beAnInstanceOf[JsString]
    results(0) \ "message" must beAnInstanceOf[JsString]
    (results(0) \ "id").toString().stripPrefix("\"").stripSuffix("\"")
  }

  "test list folders" in new WithServer {
    Seq("inbox","later","trash","sent") map checkFolders
  }

  "test create mail" in new WithServer {
    val url = "http://localhost:9000/mails"
    val json = Json.obj("contact" -> "testContact", "subject" -> "testSubject", "message" -> "testMessage", "folder" -> "sent")
    val response = await(WS.url(url).withHeaders(CONTENT_TYPE -> "application/json", ACCEPT -> "application/json").post(json))
    response.status must equalTo(OK)
    response.json \ "status" must beEqualTo(JsString("OK"))
  }

  "test update mail" in new WithServer {
    val testId = checkFolders("inbox")
    val url = s"http://localhost:9000/mails/sent/${testId}"
    val response = await(WS.url(url).withHeaders(CONTENT_TYPE -> "application/json", ACCEPT -> "application/json").get())
    response.status must equalTo(OK)
    response.json \ "status" must beEqualTo(JsString("OK"))
  }

  "test delete mail" in new WithServer {
    val testId = checkFolders("later")
    val url = s"http://localhost:9000/mails/${testId}"
    val response = await(WS.url(url).delete())
    response.status must equalTo(OK)
    response.json \ "status" must beEqualTo(JsString("OK"))
  }
}