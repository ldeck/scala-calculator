import Dependencies._

ThisBuild / scalaVersion     := "3.1.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "xyz.ldeck"
ThisBuild / organizationName := "ldeck"

lazy val root = (project in file("."))
  .settings(
    name := "scala-calculator",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      scalaCheck % Test,
      scalaTestPropSpec % Test
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
