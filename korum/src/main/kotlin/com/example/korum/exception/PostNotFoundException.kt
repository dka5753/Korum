package com.example.korum.exception

class PostNotFoundException (id: Long): RuntimeException("${id}번 게시글을 찾을 수 없습니다.")