package com.example.korum.dto

data class CreatePostRequest (
    val title: String,
    val content: String,
    val author: String,
)
