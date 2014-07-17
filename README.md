# ReactiveMongo Extensions Sample Application with Play! Framework 2.2 #

This is a single page web application sample for Play 2.2, enabling support for [ReactiveMongo Extensions](https://github.com/ReactiveMongo/ReactiveMongo-Extensions)

ReactiveMongo, reactive, asynchronous and non-blocking Scala driver for MongoDB. ReactiveMongo Extensions is to provide all the necessary tools for ReactiveMongo other than the core functionality.

This example use the following:

* MongoDB
* ReactiveMongo
* ReactiveMongo Extensions
* Play! Framework 2.2
* ReactiveMongo Play Plugin
* Flight JS
* Require JS
* Mustache JS

This application manages mails. A mail has a contact, subject, message. The mails can be compose, move, delete via single page. All the classic CRUD operations are implemented with ReactiveMongo Extensions.

This web application covers the following features:

* REST API
* Non-blocking queries
* Non-blocking update
* Non-blocking delete
* Sort
* Data pre-load
* Component-based, event-driven Flight JavaScript front-end

## How to run and test

### Configure your database access within `application.conf`
``` 
mongodb.servers = ["localhost:27017"]
mongodb.db = "mails"
```
### Run
Execute the following command via SBT:
``` 
run
```
### Test
Test classes in `test` folder. If you want to run tests, execute the following command via SBT:
``` 
test
```

## A glance at features with REST API, ReactiveMongo Extensions


### REST API

Actions return JSON object.

```scala
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
```

`MailFormats` provides for Scala object < - > JSON object conversion.

```scala
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
```
Make a request via REST API.

```javascript
this.serveMailItems = function (ev, data) {
    var self = this;
    var folder = (data && data.folder) || this.attr.folder;
    $.ajax({
        url: '/mails/' + folder,
        method: 'GET',
        dataType: 'json'
    }).done(function (data) {
        self.trigger("dataMailItemsServed", {markup: self.renderItems(data)}
        );
    });
};
```

#### Other REST API URLs

* GET         /mails/:folder          

 Returns mails of the mail specified by the folder parameter.
 
* GET         /mails/:folder/:id      

 Updates folder attribute of a mail of the mail specified by the id and folder parameter. Returns 
 ``` "status" ->"OK", "message" -> "Mail updated" ```

* POST        /mails                  

 Creates a mail. Returns it is succesful 
 ```"status" ->"OK", "message" -> s"Mail $mail_subject sent." otherwise "status" ->"NOT OK", "message" -> $error_message"                     ```

* DELETE      /mails/:

 Deletes mail of the mail specifed by the id parameter. Returns 
 ```"status" ->"OK", "message" -> "Mail deleted"```

### Mail Model

`Mail` presents data model via Mail case class. `Mail` object for ReactiveMongo Extensions data format.
```
case class Mail(_id : BSONObjectID = BSONObjectID.generate, contact: String,
                 folder: String, subject: String, message: String)

object Mail {
  implicit val mailFormat = Json.format[Mail]
}
```
> Please Notice:
> 
> - Your data access object may extend `JsonDao`.

> Sample:
>```scala
>object MailDao extends JsonDao[Mail, BSONObjectID](() => ReactiveMongoPlugin.db, "mails")
>```

### Query

You can use query DSL for easy query construction. `reactivemongo.extensions.json.dsl.JsonDsl` helpers provide utilities to easily construct JSON queries.

```scala
def listMails(folder: String) = {
  MailDao.findAll(selector = "folder" $eq folder, sort = "_id" $eq 1)
}
```
### Insert

ReactiveMongo extensions provides `insert` function for the object model insertion. Dead simple.

```scala
def createMail(mail: Mail) = {
  MailDao.insert(mail)
}
```

### Update

You can also use DSL with update operations.

```scala
def updateMail(folder: String, id: BSONObjectID) = {
  MailDao.updateById(id, $set("folder" -> folder))
}
```

### Delete
```scala
def deleteMail(id: BSONObjectID) = {
  MailDao.removeById(id)
}
```
### String <-> BSONObjectId Binders

Play sends request parameter as a `String`. You should convert to BSONObjectId 

```scala
implicit object pathBindableBSONObjectID
  extends play.api.mvc.PathBindable.Parsing[BSONObjectID](
    BSONObjectID(_),
    _.stringify,
    (key: String, e: Exception) =>
      "Cannot parse parameter %s as BSONObjectID: %s".format(key, e.getMessage)
  )
  
implicit object queryStringBindableBSONObjectID
  extends play.api.mvc.QueryStringBindable.Parsing[BSONObjectID](
    BSONObjectID(_),
    _.stringify,
    (key: String, e: Exception) =>
      "Cannot parse parameter %s as BSONObjectID: %s".format(key, e.getMessage)
  )
```

And you should add following lines in your `build.sbt`.

```scala
routesImport ++= Seq("extensions.Binders._", "reactivemongo.bson.BSONObjectID")
```

## A glance at features with Require, Flight and Mustache JS

### Require JS

> RequireJS is a JavaScript file and module loader. It is optimized for in-browser use, but it can be used in other JavaScript environments, like Rhino and Node. Using a modular script loader like RequireJS will improve the speed and quality of your code.

You should write a configuration file like the following lines.

```javascript
requirejs.config({
    baseUrl: 'assets/javascripts',
    paths: {
        'flight': 'bower_components/flight'
    }
});
```
And show in the Play template.

```scala
<script data-main="@routes.Assets.at("javascripts/requireMain.js")" src="@routes.Assets.at("javascripts/bower_components/requirejs/require.js")"></script>
```

### Flight JS

> Flight is a lightweight, component-based JavaScript framework that maps behavior to DOM nodes. Twitter uses it for their web applications. 

>Flight enforces strict separation of concerns. When you create a component you don't get a handle to it. Consequently, components cannot be referenced by other components and cannot become properties of the global object tree. This is by design. Components do not engage each other directly; instead, they broadcast their actions as events which are subscribed to by other components.

#### Mail compose lifecycle

Fist things first, `mail_compose_ui` listens the DOM objects and triggers the data listeners.

```javascript
this.after('initialize', function () {
    this.on(document, 'dataComposeBoxServed', this.launchComposeBox);
    this.on(document, 'uiForwardMail', this.forward);
    this.on(document, 'uiReplyToMail', this.reply);
    this.on(document, 'uiMailItemSelectionChanged', this.updateMailItemSelections);
    this.on(document, 'uiFolderSelectionChanged', this.updateFolderSelections);

    //the following bindings use delegation so that the event target is read at event time
    this.on(document, "click", {
        'cancelSelector': this.cancel,
        'sendSelector': this.requestSend,
        'newControlSelector': this.newMail
    });
    this.on('keydown', {
        'hintSelector': this.removeHint
    });
});
```

mail_compose_data take a request and realises data stuff.

```javascript
this.send = function (ev, data) {
    $.ajax({
        url: '/mails',
        method: 'POST',
        dataType: 'json',
        data: JSON.stringify(data),
        contentType: 'application/json; charset=utf-8'
    }).done(
        //this.trigger('dataMailItemsRefreshRequested', {folder: "sent"})
    );
};

this.after("initialize", function () {
    this.on("uiComposeBoxRequested", this.serveComposeBox);
    this.on("uiSendRequested", this.send);
});
```

### Mustache JS
> Mustache can be used for HTML, config files, source code - anything. It works by expanding tags in a template using values provided in a hash or object.

> We call it "logic-less" because there are no if statements, else clauses, or for loops. Instead there are only tags. Some tags are replaced with a value, some nothing, and others a series of values. This document explains the different types of Mustache tags.

You can render the template with Mustache, Hogan etc. This application uses Mustache.

List the all mails:

```html
'{{#mailItems}}\
  <tr id="{{id}}" class="mail-item">\
    <td class="span2 mailContact">{{contact}}</td>\
    <td class="span8">\
      <span class="mailSubject">\
        {{subject}}\
      </span>\
      <span class="mailMessage">\
        - <a href="#">{{message}}</a>\
      </span>\
    </td>\
  </tr>\
{{/mailItems}}';
```
```javascript
this.renderItems = function (items) {
    return Mustache.render(templates.mailItem, {mailItems: items});
};
```
