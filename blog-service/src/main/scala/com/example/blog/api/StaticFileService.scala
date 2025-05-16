package com.example.blog.api

import cats.effect.kernel.Async
import org.http4s.{ HttpRoutes, StaticFile }
import org.http4s.dsl.Http4sDsl

/**
 * Service for serving static files from resources
 */
object StaticFileService {
  /**
   * Create routes for serving static files using the provided effect type F
   */
  def routes[F[_]: Async]: HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    
    HttpRoutes.of[F] {
      // Serve index.html for the root path
      case request @ GET -> Root =>
        StaticFile
          .fromResource[F]("/static/index.html", Some(request))
          .getOrElseF(NotFound())

      // Serve index.html for paths without file extensions (SPA routing)
      case request @ GET -> Root / path if !path.contains(".") =>
        StaticFile
          .fromResource[F]("/static/index.html", Some(request))
          .getOrElseF(NotFound())

      // Serve static files for other paths
      case request @ GET -> Root / path =>
        StaticFile
          .fromResource[F](s"/static/$path", Some(request))
          .getOrElseF(NotFound())
    }
  }
}
