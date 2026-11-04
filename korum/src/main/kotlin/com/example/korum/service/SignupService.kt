package com.example.korum.service

import com.example.korum.dto.SignupRequest
import com.example.korum.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class SignupService(
    private val users: UserRepository,
    private val encoder: PasswordEncoder,
) {
    fun signup(request: SignupRequest) {
    }
}
