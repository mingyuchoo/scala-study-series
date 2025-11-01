package com.example.blog.model

import munit.FunSuite
import java.time.LocalDateTime

class PostTest extends FunSuite {
  test("Post should be created with correct values") {
    val now = LocalDateTime.now()
    val post = Post(
      id = Some(1L),
      title = "Test Title",
      content = "Test Content",
      author = "Test Author",
      createdAt = now,
      updatedAt = None
    )

    assertEquals(post.id, Some(1L))
    assertEquals(post.title, "Test Title")
    assertEquals(post.content, "Test Content")
    assertEquals(post.author, "Test Author")
    assertEquals(post.createdAt, now)
    assertEquals(post.updatedAt, None)
  }

  test("Post should be created with default createdAt and updatedAt") {
    val post = Post(
      id = None,
      title = "Test Title",
      content = "Test Content",
      author = "Test Author"
    )

    assertEquals(post.id, None)
    assertEquals(post.title, "Test Title")
    assertEquals(post.content, "Test Content")
    assertEquals(post.author, "Test Author")
    assert(post.createdAt != null)
    assertEquals(post.updatedAt, None)
  }
}
