package com.example.korum.service

import com.example.korum.dto.SignupRequest
import com.example.korum.exception.DuplicateUsernameException
import com.example.korum.exception.InvalidSignupException
import com.example.korum.model.User
import com.example.korum.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class SignupService(
    private val users: UserRepository,
    private val encoder: PasswordEncoder,
) {
    fun signup(request: SignupRequest) {
        if (request.email.isBlank() || request.username.isBlank() || request.password.length < 4) {
            throw InvalidSignupException("이메일·아이디와 4자 이상 비밀번호가 필요합니다.")
        }

        if (users.findByUsername(request.username) != null) {
            throw DuplicateUsernameException("이미 사용중인 아이디입니다.")
        }
        val user =
            User(
                email = request.email,
                username = request.username,
                password = checkNotNull(encoder.encode(request.password)),
                role = "USER",
            )
        users.save(user)
    }
}
