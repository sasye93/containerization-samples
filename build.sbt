name := "containerization-samples"

organization := "de.tuda.stg"

version := "0.0.0"

scalaVersion := "2.12.8"

scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked", "-Xlint")

resolvers += Resolver.bintrayRepo("stg-tud", "maven")

libraryDependencies ++= Seq(
  "de.tuda.stg" %% "rescala" % "0.26.0",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "de.tuda.stg" %% "scala-loci-communicator-tcp" % "0.3.0",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.6.0",
  "com.typesafe.akka" %% "akka-http" % "10.1.8",
  "com.typesafe.akka" %% "akka-stream" % "2.5.23",
  "de.tuda.stg" %% "scala-loci-lang" % "0.3.0",
  "de.tuda.stg" %% "scala-loci-serializer-upickle" % "0.3.0",
  "de.tuda.stg" %% "scala-loci-communicator-ws-akka" % "0.3.0",
  "de.tuda.stg" %% "scala-loci-communicator-webrtc" % "0.3.0",
  "de.tuda.stg" %% "scala-loci-lang-transmitter-rescala" % "0.3.0")

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.patch)

lazy val root = project.in(file("."))