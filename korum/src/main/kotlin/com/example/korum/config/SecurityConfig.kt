package com.example.korum.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@EnableMethodSecurity
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
                    ).authenticated()
                    .anyRequest()
                    .authenticated()
            }.formLogin { form ->
                form
                    .loginProcessingUrl("/api/login")
                    .successHandler { request, response, authentication ->
                        response.status = HttpStatus.OK.value()
                    }
                    .failureHandler { request, response, exception ->
                        response.status = HttpStatus.UNAUTHORIZED.value()
                    }
            }.logout { logout ->
                logout
                    .logoutUrl("/api/logout")
                    .logoutSuccessHandler { request, response, authentication ->
                        response.status = HttpStatus.OK.value()
                    }
            }
            .httpBasic { basic -> basic.disable() }
            .cors(Customizer.withDefaults())
            .exceptionHandling { exceptions ->
                exceptions.authenticationEntryPoint { request, response, exception ->
                    response.status = HttpStatus.UNAUTHORIZED.value()
                }
            }

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
