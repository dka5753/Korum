package com.example.korum.controller

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@CrossOrigin(origins = ["http://localhost:5173"])
@RestController
class MeController {
    @GetMapping("/me")
    fun getPostByMe(principal: Principal): Map<String, String> = mapOf("username" to principal.name)
}
