package com.example.blog.api

import cats.effect.kernel.Async
import cats.implicits._
import com.example.blog.model.{Post, Tags}
import com.example.blog.service.PostService
import io.circe.{ Decoder, Encoder, HCursor, Json }
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * HTTP API for blog posts using tagless final pattern
 */
class BlogApi[F[_]: Async](postService: PostService[F]) {
  // Use Http4sDsl with the generic effect type F
  private val dsl = new Http4sDsl[F]{}
  import dsl._
  
  // Custom JSON encoders/decoders for LocalDateTime
  implicit val dateTimeEncoder: Encoder[LocalDateTime] =
    Encoder.encodeString.contramap[LocalDateTime](_.toString)
  implicit val dateTimeDecoder: Decoder[LocalDateTime] =
    Decoder.decodeString.emapTry(s => scala.util.Try(LocalDateTime.parse(s)))

  // Custom encoder for Option[LocalDateTime]
  implicit val optionDateTimeEncoder: Encoder[Option[LocalDateTime]] = Encoder.instance {
    case Some(dt) => Json.fromString(dt.toString)
    case None     => Json.Null
  }

  // Custom decoder for Option[LocalDateTime]
  implicit val optionDateTimeDecoder: Decoder[Option[LocalDateTime]] = Decoder.instance { cursor =>
    if (cursor.value.isNull) Right(None)
    else dateTimeDecoder.map(Some(_)).apply(cursor)
  }
  
  // Encoders and decoders for Tags
  implicit val tagsEncoder: Encoder[Tags] = Encoder.instance { tags =>
    Json.fromString(tags.asString)
  }
  
  implicit val tagsDecoder: Decoder[Tags] = Decoder.instance { cursor =>
    cursor.as[String].map(Tags.fromString)
  }

  // Custom JSON encoder for Post
  implicit val postEncoder: Encoder[Post] = new Encoder[Post] {
    final def apply(post: Post): Json = Json.obj(
      ("id", post.id.asJson),
      ("title", Json.fromString(post.title)),
      ("content", Json.fromString(post.content)),
      ("author", Json.fromString(post.author)),
      ("tags", post.tags.asJson),
      ("createdAt", post.createdAt.asJson),
      ("updatedAt", post.updatedAt.asJson)
    )
  }

  // Custom JSON decoder for Post
  implicit val postDecoder: Decoder[Post] = new Decoder[Post] {
    final def apply(c: HCursor): Decoder.Result[Post] =
      for {
        id        <- c.downField("id").as[Option[Long]].orElse(Right(None))
        title     <- c.downField("title").as[String]
        content   <- c.downField("content").as[String]
        author    <- c.downField("author").as[String]
        tags      <- c.downField("tags").as[Tags].orElse(
                      c.downField("tags").as[String].map(Tags.fromString)).orElse(Right(Tags.empty))
        createdAt <- c.downField("createdAt").as[LocalDateTime].orElse(Right(LocalDateTime.now()))
        updatedAt <- c.downField("updatedAt").as[Option[LocalDateTime]].orElse(Right(None))
      } yield Post(
        id = id,
        title = title,
        content = content,
        author = author,
        tags = tags,
        createdAt = createdAt,
        updatedAt = updatedAt
      )
  }

  // HTTP entity encoders/decoders - using the generic effect type F
  implicit def postEntityEncoder: EntityEncoder[F, Post] = jsonEncoderOf[F, Post]
  implicit def postEntityDecoder: EntityDecoder[F, Post] = jsonOf[F, Post]

  /**
   * Define the HTTP routes for blog posts
   */
  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    // Get all posts
    case GET -> Root / "posts" =>
      for {
        posts    <- postService.getAllPosts
        response <- Ok(posts.asJson)
      } yield response

    // Get post by id
    case GET -> Root / "posts" / LongVar(id) =>
      for {
        maybePost <- postService.getPostById(id)
        response <- maybePost match {
          case Some(post) => Ok(post.asJson)
          case None       => NotFound(s"Post with id $id not found")
        }
      } yield response

    // Create a new post
    case req @ POST -> Root / "posts" =>
      for {
        post        <- req.as[Post]
        createdPost <- postService.createPost(post)
        response    <- Created(createdPost.asJson)
      } yield response

    // Update an existing post
    case req @ PUT -> Root / "posts" / LongVar(id) =>
      for {
        post        <- req.as[Post]
        updatedPost <- postService.updatePost(post.copy(id = Some(id)))
        response <- updatedPost match {
          case Some(updated) => Ok(updated.asJson)
          case None          => NotFound(s"Post with id $id not found")
        }
      } yield response

    // Delete a post
    case DELETE -> Root / "posts" / LongVar(id) =>
      for {
        deleted <- postService.deletePost(id)
        response <-
          if (deleted) Ok(s"Post $id deleted") else NotFound(s"Post with id $id not found")
      } yield response

    // Search posts
    case GET -> Root / "posts" / "search" / searchTerm =>
      for {
        posts    <- postService.searchPosts(searchTerm)
        response <- Ok(posts.asJson)
      } yield response
      
    // Find posts by tag
    case GET -> Root / "posts" / "tag" / tag =>
      for {
        posts    <- postService.findByTag(tag)
        response <- Ok(posts.asJson)
      } yield response
  }
}

/**
 * Companion object for BlogApi
 */
object BlogApi {
  /**
   * Create a new BlogApi instance
   */
  def apply[F[_]: Async](postService: PostService[F]): BlogApi[F] = 
    new BlogApi[F](postService)
}
