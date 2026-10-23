package com.example.korum.model

data class Post(
    val id:Long,
    val title: String,
    val content: String,
    val author: String,
)

data class UpdatedPostRequest (
    val title: String
)