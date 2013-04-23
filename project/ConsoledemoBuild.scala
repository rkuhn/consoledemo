import sbt._
import sbt.Keys._

object ConsoledemoBuild extends Build {

  lazy val consoledemo = Project(
    id = "consoledemo",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "ConsoleDemo",
      organization := "demo.akka",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.0",
      scalacOptions ++= Seq("-feature", "-deprecation"),
      resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.1.2",
      libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.1.2",
      libraryDependencies += "com.typesafe.atmos" % "trace-akka-2.1.1" % "1.2.0-M3" excludeAll(ExclusionRule(organization = "com.typesafe.akka")),
      resolvers += "Atmos Repo" at "http://repo.typesafe.com/typesafe/atmos-releases/",
      credentials <+= baseDirectory map ((b) => Credentials(b / "lib_managed/credentials"))
    )
  )
}
