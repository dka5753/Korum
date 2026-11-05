package com.example.korum.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        HttpMethod.GET,
                        "/posts",
                        "/posts/**",
                        "/csrf",
                    ).permitAll()
                    .requestMatchers(
                        HttpMethod.POST,
                        "/signup",
                    ).permitAll()
                    .requestMatchers(
                        "/admin/**",
                    ).hasRole("ADMIN")
                    .anyRequest()
                    .authenticated()
            }.formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults())
            .cors(Customizer.withDefaults())

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
