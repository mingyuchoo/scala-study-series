package com.example.blog.config

import cats.effect.kernel.{ Async, Resource }
import cats.effect.std.Console
import doobie.h2.H2Transactor
import doobie.util.ExecutionContexts

/**
 * Database configuration
 */
final case class DatabaseConfig(
    driver: String = "org.h2.Driver",
    url: String = "jdbc:h2:mem:blog;DB_CLOSE_DELAY=-1",
    user: String = "sa",
    password: String = ""
)

/**
 * Server configuration
 */
final case class ServerConfig(
    host: String = "0.0.0.0",
    port: Int = 8080
)

/**
 * Application configuration
 */
final case class AppConfig(
    database: DatabaseConfig = DatabaseConfig(),
    server: ServerConfig = ServerConfig()
)

/**
 * Configuration utilities
 */
object AppConfig {
  /**
   * Create a database transactor with the given configuration
   */
  def createTransactor[F[_]: Async: Console](
      config: DatabaseConfig
  ): Resource[F, H2Transactor[F]] =
    for {
      _ <- Resource.eval(Console[F].println("Creating database connection pool..."))
      ce <- ExecutionContexts.fixedThreadPool[F](10)
      xa <- H2Transactor.newH2Transactor[F](
        config.url,
        config.user,
        config.password,
        ce
      )
    } yield xa
}
