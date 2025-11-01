package com.example.blog

import cats.effect.{ ExitCode, IO, IOApp, Resource }
import cats.effect.std.Console
import com.example.blog.api.{ BlogApi, StaticFileService }
import com.example.blog.config.AppConfig
import com.example.blog.repository.PostRepository
import com.example.blog.service.PostService
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.middleware.{ CORS, Logger }
import org.typelevel.log4cats.{ Logger => Log4CatsLogger }
import org.typelevel.log4cats.slf4j.Slf4jLogger

/**
 * Server application using tagless final pattern
 * Not an IOApp anymore to avoid having multiple main classes
 */
object BlogServer {
  // Create a logger for IO
  implicit private def logger: Log4CatsLogger[IO] = Slf4jLogger.getLogger[IO]
  implicit private val console: Console[IO] = Console.make[IO]

  /**
   * Run the server application
   */
  def run: IO[ExitCode] = {
    // Load application configuration
    val config = AppConfig()

    // Create and start the server
    val server = for {
      _          <- Resource.eval(logger.info("Starting Blog Server..."))
      // Create database connection
      transactor <- AppConfig.createTransactor[IO](config.database)
      _          <- Resource.eval(logger.info("Database connection established"))
      // Initialize database schema
      _          <- Resource.eval(PostRepository.initializeDb[IO](transactor))
      _          <- Resource.eval(logger.info("Database schema initialized"))
      // Create repository and service instances
      postRepo   <- PostRepository.make[IO](transactor)
      postService = PostService[IO](postRepo)
      // Create API instance
      blogApi     = BlogApi[IO](postService)

      // Combine API routes with static file routes
      routes = Router(
        "/api" -> blogApi.routes,
        "/"    -> StaticFileService.routes[IO]
      ).orNotFound

      // Add CORS support and logging middleware
      httpApp = Logger.httpApp(logHeaders = true, logBody = true)(
        CORS.policy.withAllowOriginAll(routes)
      )

      // Start the HTTP server
      _ <- Resource.eval(logger.info(s"Starting server on port ${config.server.port}"))
      server <- EmberServerBuilder
        .default[IO]
        .withHost(com.comcast.ip4s.Host.fromString("0.0.0.0").get)
        .withPort(com.comcast.ip4s.Port.fromInt(config.server.port).get)
        .withHttpApp(httpApp)
        .build
    } yield server

    // Run the server forever
    server.use(_ => IO.never).as(ExitCode.Success)
  }
  
  /**
   * Main method for compatibility with IOApp
   */
  def main(args: Array[String]): Unit = {
    import cats.effect.unsafe.implicits.global
    run.unsafeRunSync()
  }
}
