package com.example.korum.dto

import com.example.korum.model.Post

fun Post.toResponse(): PostResponse =
    PostResponse (
        this.id?:error("id가 없습니다."),
        this.title,
        this.content,
        this.author
    )