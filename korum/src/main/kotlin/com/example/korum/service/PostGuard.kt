package com.example.korum.service

import com.example.korum.repository.PostRepository
import org.springframework.stereotype.Component

@Component("postGuard")
class PostGuard(
    private val postRepository: PostRepository
) {
    fun isAuthor(id: Long, username: String): Boolean {
        val post = postRepository.findById(id).orElse(null)
            ?: return false

        return post.author == username
    }
}