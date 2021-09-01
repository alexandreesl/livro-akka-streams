
name := "akka-streams"

version := "1.0"

scalaVersion := "2.13.6"
val AkkaVersion = "2.6.15"
val slickVersion = "3.3.3"
val akkaHttpVersion = "10.2.6"

mainClass in Compile := Some("com.casadocodigo.Boot")

//Akka e outras dependencias
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test
)

// dependencias de logging
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

// dependencias do Alpakka
libraryDependencies ++= Seq(
  "com.lightbend.akka" %% "akka-stream-alpakka-file" % "3.0.3",
  "com.typesafe.akka" %% "akka-stream-kafka" % "2.1.1",
  "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "3.0.3",
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "3.0.3"
)

// dependencias de banco
libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "42.2.23"
)
