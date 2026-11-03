package com.example.korum.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminController {
    @GetMapping("/admin/check")
    fun admin(): String = "관리자 기능"
}
