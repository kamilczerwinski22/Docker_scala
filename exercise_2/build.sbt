name := "exercise_2"

version := "1.0"

lazy val ex_2 = (project in file(".")).enablePlugins(PlayScala)


resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"

scalaVersion := "2.12.12"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )
      