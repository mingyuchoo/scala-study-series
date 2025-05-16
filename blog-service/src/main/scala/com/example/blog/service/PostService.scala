package com.example.blog.service

import cats.Monad
import cats.effect.kernel.Async
import cats.implicits._
import com.example.blog.model.{Post, Tags}
import com.example.blog.repository.PostRepository

/**
 * Service interface for blog post operations
 */
trait PostService[F[_]] {
  /**
   * Get all posts
   */
  def getAllPosts: F[List[Post]]
  
  /**
   * Get a post by its ID
   */
  def getPostById(id: Long): F[Option[Post]]
  
  /**
   * Create a new post
   */
  def createPost(post: Post): F[Post]
  
  /**
   * Update an existing post
   */
  def updatePost(post: Post): F[Option[Post]]
  
  /**
   * Delete a post by ID
   */
  def deletePost(id: Long): F[Boolean]
  
  /**
   * Search posts by term (matches title, content, author, or tags)
   */
  def searchPosts(term: String): F[List[Post]]
  
  /**
   * Find posts by tag
   */
  def findByTag(tag: String): F[List[Post]]
}

/**
 * Default implementation of PostService
 */
class PostServiceImpl[F[_]: Monad](repository: PostRepository[F]) extends PostService[F] {
  def getAllPosts: F[List[Post]] = repository.findAll

  def getPostById(id: Long): F[Option[Post]] = repository.findById(id)

  def createPost(post: Post): F[Post] = repository.create(post)

  def updatePost(post: Post): F[Option[Post]] = repository.update(post)

  def deletePost(id: Long): F[Boolean] = repository.delete(id)

  def searchPosts(term: String): F[List[Post]] = repository.search(term)
  
  def findByTag(tag: String): F[List[Post]] = 
    repository.findAll.map(_.filter(_.tags.contains(tag)))
}

/**
 * Companion object for PostService
 */
object PostService {
  /**
   * Create a new PostService instance
   */
  def apply[F[_]: Async](repository: PostRepository[F]): PostService[F] = 
    new PostServiceImpl[F](repository)
}
