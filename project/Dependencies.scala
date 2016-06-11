import sbt._

object Version {
  final val Scala     = "2.11.8"
  final val ScalaTest = "3.0.0-RC1"
}

object Library {
  val scalaTest = "org.scalatest" %% "scalatest" % Version.ScalaTest
  val config = "com.typesafe" % "config" % "1.3.0"

}
