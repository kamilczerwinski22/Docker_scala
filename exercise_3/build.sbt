name := "exercise_3"
 
version := "1.0" 
      
lazy val `ex_3` = (project in file(".")).enablePlugins(PlayScala)

      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.13"

libraryDependencies ++= Seq(
  ehcache , ws , specs2 % Test , guice,
  "org.xerial" % "sqlite-jdbc" % "3.36.0.2",
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0")