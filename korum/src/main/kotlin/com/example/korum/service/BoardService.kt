package com.example.korum.service

import com.example.korum.dto.CreatePostRequest
import com.example.korum.dto.PostResponse
import org.springframework.stereotype.Service
import com.example.korum.model.Post
import com.example.korum.repository.PostRepository
import com.example.korum.exception.PostNotFoundException

@Service
class BoardService (private val postRepository: PostRepository )  {
    
    fun getWelcomeMessage () : String = "게시판에 오신 여러분을 환영합니다."

    fun getAllPost () : List<Post>  = postRepository.findAll()

    fun getPostById(id: Long): Post = postRepository.findById(id) ?: throw PostNotFoundException(id)
    
    fun createPost(post: CreatePostRequest): Post{
        val post = Post(
            id = 0L,
            title = post.title,
            content = post.content,
            author = post.author,
        )
        return postRepository.save(post)
    }

    fun updatePost(id: Long, title: String): Post = postRepository.update(id, title) ?: throw PostNotFoundException(id)

    fun deletePost(id: Long) {
        val deleted = postRepository.deleteById(id)
        if (!deleted) {
            throw PostNotFoundException(id)
        }
    }
}