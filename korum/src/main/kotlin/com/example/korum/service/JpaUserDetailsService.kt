package com.example.korum.service

import com.example.korum.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.security.core.userdetails.User as SecurityUser

@Service
class JpaUserDetailsService(
    private val users: UserRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = users.findByUsername(username) ?: throw UsernameNotFoundException(username)
        return SecurityUser
            .withUsername(user.username)
            .password(user.password)
            .roles(user.role)
            .build()
    }
}
