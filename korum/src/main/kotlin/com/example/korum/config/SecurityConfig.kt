package com.example.korum.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
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
    fun userDetailsService(): UserDetailsService {
        val admin =
            User
                .withUsername("admin")
                .password("{noop}1234")
                .roles("ADMIN")
                .build()
        val user =
            User
                .withUsername("user")
                .password("{noop}1234")
                .roles("USER")
                .build()

        return InMemoryUserDetailsManager(user, admin)
    }
}
