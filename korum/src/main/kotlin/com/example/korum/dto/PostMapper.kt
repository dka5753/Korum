package com.example.korum.dto

import com.example.korum.model.Post

fun Post.toResponse(): PostResponse =
    PostResponse (
        this.id,
        this.title,
        this.content,
        this.author
    )