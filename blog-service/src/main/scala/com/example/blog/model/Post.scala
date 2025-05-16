package com.example.blog.model

import java.time.LocalDateTime

/**
 * Represents a blog post in the system
 * 
 * @param id Unique identifier, None for new posts
 * @param title Post title
 * @param content Post content/body
 * @param author Name of the post author
 * @param tags List of tags associated with the post
 * @param createdAt When the post was created
 * @param updatedAt When the post was last updated, if ever
 */
final case class Post(
    id: Option[Long],
    title: String,
    content: String,
    author: String,
    tags: Tags = Tags.empty,
    createdAt: LocalDateTime = LocalDateTime.now(),
    updatedAt: Option[LocalDateTime] = None
)

/**
 * Type-safe representation of post tags
 */
final case class Tags(values: List[String]) extends AnyVal {
  def asString: String = values.mkString(";")
  def isEmpty: Boolean = values.isEmpty
  def nonEmpty: Boolean = values.nonEmpty
  def contains(tag: String): Boolean = values.contains(tag)
  def +(tag: String): Tags = Tags(values :+ tag)
  def ++(other: Tags): Tags = Tags(values ++ other.values)
  def -(tag: String): Tags = Tags(values.filterNot(_ == tag))
}

object Tags {
  val empty: Tags = Tags(List.empty)
  
  def fromString(s: String): Tags = 
    if (s.isEmpty) empty 
    else Tags(s.split(";").toList.map(_.trim).filter(_.nonEmpty))
    
  def apply(tags: String*): Tags = Tags(tags.toList)
}
