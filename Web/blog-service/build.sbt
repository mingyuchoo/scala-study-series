val scala3Version = "3.8.4"

lazy val root = project
  .in(file("."))
  .settings(
    name := "blog-service",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
    
    // Set the main class to runBlogServer only
    Compile / mainClass := Some("runBlogServer"),
    
    libraryDependencies ++= Seq(
      // Http4s
      "org.http4s" %% "http4s-ember-server" % "0.23.25",
      "org.http4s" %% "http4s-ember-client" % "0.23.25",
      "org.http4s" %% "http4s-circe" % "0.23.25",
      "org.http4s" %% "http4s-dsl" % "0.23.25",
      
      // Circe for JSON handling
      "io.circe" %% "circe-generic" % "0.14.6",
      "io.circe" %% "circe-parser" % "0.14.6",
      
      // Doobie for database access
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC4",
      "org.tpolecat" %% "doobie-h2" % "1.0.0-RC4", // H2 driver for simplicity
      
      // Logging
      "org.typelevel" %% "log4cats-slf4j" % "2.6.0",
      "ch.qos.logback" % "logback-classic" % "1.4.11",
      
      // Testing
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.typelevel" %% "munit-cats-effect" % "2.0.0" % Test
    )
  )
