package com.portal.hrms.authentication

import com.portal.hrms.dto.ErrorResponse
import com.portal.hrms.dto.LoginRequest
import com.portal.hrms.dto.LoginResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.portal.hrms.domain.AuthToken
import com.portal.hrms.utils.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class JwtAuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil
): UsernamePasswordAuthenticationFilter() {

    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        var authToken: Authentication? = null
        if (request != null) {
            val credentials = ObjectMapper().readValue(request.inputStream, LoginRequest::class.java)
            authToken = UsernamePasswordAuthenticationToken(credentials.username, credentials.password)
        }

        return authenticationManager.authenticate(authToken)
    }

    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain?,
        authResult: Authentication?
    ) {
        authResult?.let {
            val userDetails: UserDetails = it.principal as UserDetails
            val grantedRoles: MutableCollection<out GrantedAuthority> = userDetails.authorities
            val grantedRolesList: List<String> = grantedRoles.map { role -> role.authority }

            val authToken: AuthToken = jwtUtil.generateToken(userDetails)
            val token: String = authToken.token

            val loginResponse = LoginResponse(
                username = authToken.username,
                authorities = grantedRolesList,
                bearerToken = token,
                createdAt = authToken.createdAt,
                expiresAt = authToken.expiresAt
            )

            response?.apply {
                addHeader("Authorization", "Bearer $token")
                contentType = "application/json"
                characterEncoding = "UTF-8"
                writer.write(ObjectMapper().writeValueAsString(loginResponse))
                writer.flush()
            }
        }
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        failed: AuthenticationException?
    ) {
        response?.apply {
            val errorResponse = ErrorResponse(
                statusMessage = "Invalid Credentials",
                statusCode = HttpServletResponse.SC_UNAUTHORIZED
            )

            status = HttpServletResponse.SC_UNAUTHORIZED
            contentType = "application/json"
            characterEncoding = "UTF-8"
            writer.write(ObjectMapper().writeValueAsString(errorResponse))
        }
    }
}