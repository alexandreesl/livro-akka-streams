name := "akka-streams"

version := "1.0"

scalaVersion := "2.13.6"
val AkkaVersion = "2.6.15"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "io.spray" %% "spray-json" % "1.3.6",
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "org.postgresql" % "postgresql" % "42.2.23",
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test
)


// dependencias de logging
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)