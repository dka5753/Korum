package com.example.korum.controller

import com.example.korum.dto.SignupRequest
import com.example.korum.service.SignupService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@ResponseStatus(HttpStatus.CREATED)
class SignupController(
    private val signupService: SignupService,
) {
    @PostMapping("/signup")
    fun signupController(
        @RequestBody request: SignupRequest,
    ) {
        signupService.signup(request)
    }
}
