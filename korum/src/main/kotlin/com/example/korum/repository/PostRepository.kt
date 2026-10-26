package com.example.korum.repository

import com.example.korum.model.Post
import org.springframework.stereotype.Repository

@Repository
class PostRepository {
    private val posts = mutableListOf(
    Post(1L, "첫 글", "내용1", "admin"),
    Post(2L, "두 번째 글", "내용2", "user")
)
    fun findAll(): List<Post> = posts

    fun findById(id:Long): Post? = posts.find{it.id == id}

    fun save(post:Post):Post {
        val nextId = (posts.maxOfOrNull{it.id} ?: 0L) + 1L
        val savedPosts = post.copy(id = nextId)
        posts.add(savedPosts)
        return post
    }

    fun update(id: Long, title: String): Post? {
        //findById(id)를 이용하는 방식도 물어봐야지
        val post = findById(id) ?: return null
        val index = posts.indexOf(post)
        val updatePost = post.copy(
            title = title
        )
        posts[index] = updatePost
        return updatePost


        // val index = posts.indexOfFirst { it.id == id }
        // if (index == -1) return null
        // val updatedPost = posts[index].copy(title = title)
        // posts[index] = updatedPost
        // return updatePost
    }

    fun deleteById(id: Long): Boolean {
        return posts.removeIf {it.id == id}
    }
}