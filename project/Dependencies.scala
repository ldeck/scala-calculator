import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.11"
  lazy val scalaCheck = "org.scalatestplus" %% "scalacheck-1-15" % "3.2.9.0"
  lazy val scalaTestPropSpec = "org.scalatest" %% "scalatest-propspec" % "3.2.11"
}
