name := "dgraph-scala"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "io.dgraph" % "dgraph4j" % "1.7.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "com.typesafe" % "config" % "1.3.2",
  "com.google.code.gson" % "gson" % "2.8.5",
  "ch.qos.logback" % "logback-classic" % "1.1.2"
)
