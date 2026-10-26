package com.example.korum.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import com.example.korum.model.UpdatedPostRequest
import com.example.korum.service.BoardService
import com.example.korum.dto.CreatePostRequest
import com.example.korum.model.Post
import com.example.korum.dto.PostResponse
import com.example.korum.dto.toResponse

@CrossOrigin(origins = ["http://localhost:5174"])
@RestController
@RequestMapping("/posts")
class PostController(private val boardService: BoardService)
{
    @GetMapping
    fun getPosts(): List<Post> = boardService.getAllPost()

    @GetMapping("/{id}")
    fun getPostById(@PathVariable id: Long): Post = boardService.getPostById(id)

    @PostMapping
    fun createPost(
        @RequestBody request: CreatePostRequest): ResponseEntity<PostResponse>{
        val savedPost = boardService.createPost(request);
        val createdPost = savedPost.toResponse()

        return ResponseEntity.
                status(HttpStatus.CREATED).body(createdPost)
    }

    @PutMapping("/{id}")
    fun updatePost(
        @PathVariable id: Long,
        @RequestBody request: UpdatedPostRequest
    ): Post = boardService.updatePost(id, request.title)

    @DeleteMapping("/{id}")
    fun deletePost(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        boardService.deletePost(id)

        return ResponseEntity.noContent().build<Void> ()
    } 
}