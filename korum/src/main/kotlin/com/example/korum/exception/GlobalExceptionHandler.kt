package com.example.korum.exception

import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(PostNotFoundException::class)
    fun handlePostNotFound(
        ex: PostNotFoundException
    ): ResponseEntity<String> = ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
}