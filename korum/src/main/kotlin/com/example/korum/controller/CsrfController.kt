package com.example.korum.controller

import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CsrfController {
    @GetMapping("/csrf")
    fun csrfController(
        token: CsrfToken
    ): CsrfToken {
        return token
    }
}