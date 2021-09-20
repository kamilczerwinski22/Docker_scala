import play.sbt.PlayScala

name := "f1-store"

version := "1.0"

lazy val `store` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"

scalaVersion := "2.12.13"

libraryDependencies ++= Seq(
  ehcache, ws, specs2 % Test, guice,
  "com.atlassian.jwt" % "jwt-api" % "3.2.1" % "provided",
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
  "org.xerial" % "sqlite-jdbc" % "3.36.0.2",
  "com.iheart" %% "ficus" % "1.5.0",
  "com.mohiva" %% "play-silhouette" % "7.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "7.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "7.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "7.0.0",
  "com.mohiva" %% "play-silhouette-totp" % "7.0.0",
  "net.codingwell" %% "scala-guice" % "5.0.1",
)