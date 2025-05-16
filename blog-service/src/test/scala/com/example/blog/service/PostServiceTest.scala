package com.example.blog.service

import cats.effect.IO
import cats.effect.testing.munit.CatsEffectSuite
import com.example.blog.model.Post
import com.example.blog.repository.PostRepository
import java.time.LocalDateTime

class PostServiceTest extends CatsEffectSuite {

  test("getAllPosts should return all posts") {
    // Setup
    val mockPosts = List(
      Post(Some(1L), "Title 1", "Content 1", "Author 1"),
      Post(Some(2L), "Title 2", "Content 2", "Author 2")
    )

    val mockRepo = new PostRepository {
      def findAll: IO[List[Post]]              = IO.pure(mockPosts)
      def findById(id: Long): IO[Option[Post]] = IO.pure(mockPosts.find(_.id.contains(id)))
      def create(post: Post): IO[Post]         = IO.pure(post.copy(id = Some(3L)))
      def update(post: Post): IO[Option[Post]] = IO.pure(Some(post))
      def delete(id: Long): IO[Boolean]        = IO.pure(true)
    }

    val service = new PostServiceImpl(mockRepo)

    // Test
    service.getAllPosts.map { posts =>
      assertEquals(posts, mockPosts)
      assertEquals(posts.length, 2)
    }
  }

  test("getPostById should return a post when it exists") {
    // Setup
    val mockPost = Post(Some(1L), "Title 1", "Content 1", "Author 1")

    val mockRepo = new PostRepository {
      def findAll: IO[List[Post]]              = IO.pure(List(mockPost))
      def findById(id: Long): IO[Option[Post]] = IO.pure(if (id == 1L) Some(mockPost) else None)
      def create(post: Post): IO[Post]         = IO.pure(post)
      def update(post: Post): IO[Option[Post]] = IO.pure(Some(post))
      def delete(id: Long): IO[Boolean]        = IO.pure(true)
    }

    val service = new PostServiceImpl(mockRepo)

    // Test
    service.getPostById(1L).map { postOption =>
      assert(postOption.isDefined)
      assertEquals(postOption.get, mockPost)
    }
  }

  test("getPostById should return None when post doesn't exist") {
    // Setup
    val mockRepo = new PostRepository {
      def findAll: IO[List[Post]]              = IO.pure(List())
      def findById(id: Long): IO[Option[Post]] = IO.pure(None)
      def create(post: Post): IO[Post]         = IO.pure(post)
      def update(post: Post): IO[Option[Post]] = IO.pure(Some(post))
      def delete(id: Long): IO[Boolean]        = IO.pure(true)
    }

    val service = new PostServiceImpl(mockRepo)

    // Test
    service.getPostById(999L).map(postOption => assert(postOption.isEmpty))
  }

  test("createPost should return post with ID") {
    // Setup
    val postToCreate = Post(None, "New Title", "New Content", "New Author")
    val createdPost  = Post(Some(1L), "New Title", "New Content", "New Author")

    val mockRepo = new PostRepository {
      def findAll: IO[List[Post]]              = IO.pure(List())
      def findById(id: Long): IO[Option[Post]] = IO.pure(None)
      def create(post: Post): IO[Post]         = IO.pure(createdPost)
      def update(post: Post): IO[Option[Post]] = IO.pure(Some(post))
      def delete(id: Long): IO[Boolean]        = IO.pure(true)
    }

    val service = new PostServiceImpl(mockRepo)

    // Test
    service.createPost(postToCreate).map { post =>
      assertEquals(post.id, Some(1L))
      assertEquals(post.title, "New Title")
      assertEquals(post.content, "New Content")
      assertEquals(post.author, "New Author")
    }
  }
}
