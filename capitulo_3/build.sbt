name := "akka-streams"

version := "1.0"

scalaVersion := "2.13.6"
val AkkaVersion = "2.6.15"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test
)