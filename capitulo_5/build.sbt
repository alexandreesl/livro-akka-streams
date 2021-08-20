
name := "akka-api"

version := "1.0"

scalaVersion := "2.13.6"
val AkkaVersion = "2.6.15"
val akkaHttpVersion = "10.2.6"
val slickVersion = "3.3.3"

dockerExposedPorts ++= Seq(8080)

enablePlugins(JavaAppPackaging)
enablePlugins(GatlingPlugin)

mainClass in Compile := Some("com.casadocodigo.Boot")

//Akka e outras dependencias
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
)

// dependencias de logging
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

// dependencias de banco
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "org.postgresql" % "postgresql" % "42.2.23"
)

libraryDependencies ++= Seq(
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.6.1" % "test",
  "io.gatling" % "gatling-test-framework"    % "3.6.1" % "test"
)