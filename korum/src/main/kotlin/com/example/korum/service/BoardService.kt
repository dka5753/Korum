package com.example.korum.service

import com.example.korum.dto.CreatePostRequest
import org.springframework.stereotype.Service
import com.example.korum.model.Post
import com.example.korum.repository.PostRepository
import com.example.korum.exception.PostNotFoundException
import jakarta.transaction.Transactional

@Service
class BoardService (private val postRepository: PostRepository )  {
    
    fun getWelcomeMessage () : String = "게시판에 오신 여러분을 환영합니다."

    fun getAllPost () : List<Post>  = postRepository.findAll()

    fun getPostById(id: Long): Post = postRepository.findById(id).orElseThrow{ PostNotFoundException(id) }
    
    fun createPost(request: CreatePostRequest): Post{
        val post = Post(
            title = request.title,
            content = request.content,
            author = request.author,
        )
        return postRepository.save(post)
    }
    @Transactional
    fun updatePost(id: Long, title: String): Post {
        val post = getPostById(id)
        post.title = title
        return post
    }
    @Transactional
    fun deletePost(id: Long) {
        val post =getPostById(id)

        postRepository.delete(post)
    }
}