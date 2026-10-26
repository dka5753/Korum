package com.example.korum.dto

data class PostResponse (
    val id: Long,
    val title: String,
    val content: String,
    val author: String,
) {
    constructor(id: Long, title: String, content: String, author: Any) : this(id, title, content, author)
}