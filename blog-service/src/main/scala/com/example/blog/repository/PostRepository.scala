package com.example.blog.repository

import cats.effect.{ IO, Resource }
import cats.effect.kernel.Async
import cats.implicits._
import com.example.blog.model.{Post, Tags}
import doobie._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.h2.H2Transactor
import java.time.LocalDateTime

/**
 * Repository interface for Post entities using tagless final pattern
 */
trait PostRepository[F[_]] {
  def findAll: F[List[Post]]
  def findById(id: Long): F[Option[Post]]
  def create(post: Post): F[Post]
  def update(post: Post): F[Option[Post]]
  def delete(id: Long): F[Boolean]
  def search(term: String): F[List[Post]]
}

/**
 * Doobie implementation of the PostRepository trait
 */
class DoobiePostRepository[F[_]: Async](xa: Transactor[F]) extends PostRepository[F] {
  import PostRepository._

  def findAll: F[List[Post]] =
    sql"SELECT id, title, content, author, tags, created_at, updated_at FROM posts"
      .query[Post]
      .to[List]
      .transact(xa)

  def findById(id: Long): F[Option[Post]] =
    sql"SELECT id, title, content, author, tags, created_at, updated_at FROM posts WHERE id = $id"
      .query[Post]
      .option
      .transact(xa)

  def create(post: Post): F[Post] = {
    // Convert Tags to String for storage outside the for comprehension
    val tagsString = post.tags.asString
    for {
      id <-
        sql"INSERT INTO posts (title, content, author, tags, created_at) VALUES (${post.title}, ${post.content}, ${post.author}, $tagsString, ${post.createdAt})".update
          .withUniqueGeneratedKeys[Long]("id")
          .transact(xa)
    } yield post.copy(id = Some(id))
  }

  def update(post: Post): F[Option[Post]] = {
    // Convert Tags to String for storage outside the for comprehension
    val tagsString = post.tags.asString
    for {
      now <- Async[F].delay(LocalDateTime.now())
      rowsAffected <-
        sql"UPDATE posts SET title = ${post.title}, content = ${post.content}, author = ${post.author}, tags = $tagsString, updated_at = $now WHERE id = ${post.id
            .getOrElse(0L)}".update.run
          .transact(xa)
      updated <-
        if (rowsAffected > 0) Async[F].pure(Some(post.copy(updatedAt = Some(now)))) else Async[F].pure(None)
    } yield updated
  }

  def delete(id: Long): F[Boolean] =
    sql"DELETE FROM posts WHERE id = $id".update.run
      .transact(xa)
      .map(_ > 0)

  def search(term: String): F[List[Post]] = {
    val searchTerm = s"%$term%"
    sql"""
      SELECT id, title, content, author, tags, created_at, updated_at 
      FROM posts 
      WHERE LOWER(title) LIKE LOWER($searchTerm) 
      OR LOWER(content) LIKE LOWER($searchTerm) 
      OR LOWER(author) LIKE LOWER($searchTerm) 
      OR LOWER(tags) LIKE LOWER($searchTerm)
    """
      .query[Post]
      .to[List]
      .transact(xa)
  }
}

object PostRepository {
  // Meta instances for custom types
  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[java.sql.Timestamp].imap(ts => ts.toLocalDateTime)(ldt => java.sql.Timestamp.valueOf(ldt))
    
  // Convert between Tags and String for database storage
  implicit val tagsMeta: Meta[Tags] =
    Meta[String].imap(Tags.fromString)(_.asString)

  /**
   * Initialize the database schema
   */
  def initializeDb[F[_]: Async](xa: Transactor[F]): F[Unit] = {
    val createPostsTable =
      sql"""
        CREATE TABLE IF NOT EXISTS posts (
          id BIGINT AUTO_INCREMENT PRIMARY KEY,
          title VARCHAR(255) NOT NULL,
          content TEXT NOT NULL,
          author VARCHAR(100) NOT NULL,
          tags VARCHAR(500),
          created_at TIMESTAMP NOT NULL,
          updated_at TIMESTAMP
        )
      """.update.run

    createPostsTable.transact(xa).void
  }

  /**
   * Create a new PostRepository instance
   */
  def make[F[_]: Async](xa: Transactor[F]): Resource[F, PostRepository[F]] =
    Resource.pure(new DoobiePostRepository[F](xa))
}
