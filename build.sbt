ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "Note-App-Scala",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.0.21",
      "dev.zio" %% "zio-http" % "3.0.0-RC4",
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC4",
      "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC4",
      "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC4",
      "org.postgresql" % "postgresql" % "42.7.3",
      "com.typesafe" % "config" % "1.4.2"
    )
  )
