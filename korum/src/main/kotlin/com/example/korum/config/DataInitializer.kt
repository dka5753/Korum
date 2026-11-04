package com.example.korum.config

import com.example.korum.model.User
import com.example.korum.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val users: UserRepository,
    private val encoder: PasswordEncoder,
) : CommandLineRunner {
    override fun run(vararg args: String) {
        if (users.findByUsername("user") == null) {
            val user =
                User(
                    email = "user@example.test",
                    username = "user",
                    password = checkNotNull(encoder.encode("1234")),
                    role = "USER",
                )

            users.save(user)
        }
        if (users.findByUsername("admin") == null) {
            val admin =
                User(
                    email = "admin@example.test",
                    username = "admin",
                    password = checkNotNull(encoder.encode("1234")),
                    role = "ADMIN",
                )
            users.save(admin)
        }
    }
}
