package com.example.korum.service

import com.example.korum.dto.CreatePostRequest
import com.example.korum.exception.PostNotFoundException
import com.example.korum.model.Post
import com.example.korum.repository.PostRepository
import jakarta.transaction.Transactional
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class BoardService(
    private val postRepository: PostRepository,
) {
    fun getWelcomeMessage(): String = "게시판에 오신 여러분을 환영합니다."

    fun getAllPost(): List<Post> = postRepository.findAll()

    fun getPostById(id: Long): Post = postRepository.findById(id).orElseThrow { PostNotFoundException(id) }

    fun createPost(request: CreatePostRequest, author: String): Post {
        val post =
            Post(
                title = request.title,
                content = request.content,
                author = author,
            )
        return postRepository.save(post)
    }

    @PreAuthorize("@postGuard.isAuthor(#id, authentication.name) or hasRole('ADMIN')")
    @Transactional
    fun updatePost(
        id: Long,
        title: String,
    ): Post {
        val post = getPostById(id)
        post.title = title
        return post
    }

    @PreAuthorize("@postGuard.isAuthor(#id, authentication.name) or hasRole('ADMIN')")
    @Transactional
    fun deletePost(id: Long) {
        val post = getPostById(id)

        postRepository.delete(post)
    }
}
