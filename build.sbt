import PlayKeys._

name := "reactivemongo-extensions-sampleapp"

version := "1.0"

scalaVersion := "2.11.2"

routesImport ++= Seq("extensions.Binders._", "reactivemongo.bson.BSONObjectID")

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  ws,
  "org.reactivemongo" %% "reactivemongo-extensions-json" % "0.10.5.akka23-SNAPSHOT")

lazy val root = (project in file(".")).enablePlugins(PlayScala)
