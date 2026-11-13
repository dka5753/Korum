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
import org.springframework.security.web.csrf.CsrfException

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
                        response.contentType = "application/json;charset=UTF-8"
                        response.writer.write("""{"error" : "아이디와 비밀번호를 확인해주세요"}""")
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
                    response.contentType= "application/json;charset=UTF-8"
                    response.writer.write("""{"error": "로그인이 필요합니다."}""")
                }
                exceptions.accessDeniedHandler { request, response, exception ->
                    response.status = HttpStatus.FORBIDDEN.value()
                    response.contentType = "application/json;charset=UTF-8"
                    val message = if(exception is CsrfException) {
                        "CSRF 토큰이없습니다"
                    }else {
                        "권한이 없습니다"
                    }
                    response.writer.write("""{"error": "$message"}""")
                }
            }

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
