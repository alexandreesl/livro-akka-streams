
name := "akka-streams"

version := "1.0"

scalaVersion := "2.13.6"
val AkkaVersion = "2.6.15"

enablePlugins(JavaAppPackaging)

mainClass in Compile := Some("com.casadocodigo.Boot")

//Akka e outras dependencias
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
  "io.spray" %% "spray-json" % "1.3.6"
)

// dependencias de logging
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

// dependencias de banco
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "org.postgresql" % "postgresql" % "42.2.23"
)