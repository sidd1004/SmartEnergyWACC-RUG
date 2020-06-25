name := """back-end"""
organization := "rug.wacc"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.7"

enablePlugins(DockerPlugin)
version in Docker := "latest"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
libraryDependencies += "com.typesafe.play" %% "play" % "2.7.3"
libraryDependencies += "com.typesafe.play" %% "twirl-api" % "1.5.0-M4"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.7.3"
libraryDependencies += "com.typesafe.play" %% "play-server" % "2.7.3"
libraryDependencies += "com.typesafe.play" %% "play-logback" % "2.7.3"
libraryDependencies += "com.typesafe.play" %% "play-netty-server" % "2.7.3"
libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.18.6-play27"
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.12.1"
libraryDependencies += "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.12.1"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.12.1"
libraryDependencies += "org.apache.logging.log4j" % "log4j-web" % "2.12.1"
libraryDependencies += "org.apache.kafka" %% "kafka" % "2.3.0"
libraryDependencies += "org.apache.kafka" % "kafka-streams" % "2.3.0"
libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % "2.3.0"
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "3.7.2"
libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "1.0.5"
libraryDependencies += "com.outworkers" %% "phantom-dsl" % "2.14.5"
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "3.7.2"
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-mapping" % "3.7.2"
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-extras" % "3.7.2"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
play.sbt.routes.RoutesKeys.routesImport += "play.modules.reactivemongo.PathBindables._"
routesGenerator := InjectedRoutesGenerator

javaOptions in Universal ++= Seq(
  "-Dpidfile.path=/dev/null"
)

/*
import com.typesafe.sbt.packager.docker._

dockerCommands := Seq(
  ExecCmd("CMD", "echo", "Hello, World from Docker")
)*/
