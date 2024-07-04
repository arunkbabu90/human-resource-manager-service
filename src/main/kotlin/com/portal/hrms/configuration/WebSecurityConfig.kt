package com.portal.hrms.configuration

import com.portal.hrms.authentication.JwtAuthenticationFilter
import com.portal.hrms.authentication.JwtAuthorizationFilter
import com.portal.hrms.utils.JwtUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: UserDetailsService
) {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity, authenticationManager: AuthenticationManager): SecurityFilterChain {
        return with(http) {
            authorizeHttpRequests {
                it.requestMatchers("/login").permitAll()
                    .requestMatchers("/auth/**").permitAll()
                    .anyRequest().authenticated()
            }
            csrf { it.disable() }
            addFilter(JwtAuthenticationFilter(authenticationManager, jwtUtil))
            addFilter(JwtAuthorizationFilter(authenticationManager, jwtUtil))
        }.build()
    }

    @Bean
    fun authenticationManager(http: HttpSecurity): AuthenticationManager {
        val builder = http.getSharedObject(AuthenticationManagerBuilder::class.java).also {
            it.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder())
        }

        return builder.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

}