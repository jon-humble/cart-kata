lazy val cart = project
  .copy(id = "cart")
  .in(file("."))
  .enablePlugins(AutomateHeaderPlugin, GitVersioning)

name := "cart"

libraryDependencies ++= Vector(
  Library.scalaTest % "test"
)

initialCommands := """|import default.cart._
                      |""".stripMargin
