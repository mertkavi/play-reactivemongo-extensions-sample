import play.Project._

name := "reactivemongo-extensions-sampleapp"

version := "1.0"

playScalaSettings

routesImport ++= Seq("extensions.Binders._", "reactivemongo.bson.BSONObjectID")

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "reactivemongo-extensions-json" % "0.10.0.0-SNAPSHOT")
