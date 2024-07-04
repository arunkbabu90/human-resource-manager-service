package com.portal.hrms.authentication

import com.auth0.jwt.exceptions.TokenExpiredException
import com.portal.hrms.dto.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.portal.hrms.utils.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

class JwtAuthorizationFilter(
    authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil
): BasicAuthenticationFilter(authenticationManager) {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header = request.getHeader("Authorization")
         if (header == null || !header.startsWith("Bearer ")) {
             // No Valid Authorization Header found
             chain.doFilter(request, response)
             return
         } else {
             // Authorization Header found. Do the validation and the rest
             val token = header.substring(7)
             try {
                 jwtUtil.validateToken(token).also {
                     val authToken = UsernamePasswordAuthenticationToken(it.username, null, it.authorities)
                     SecurityContextHolder.getContext().authentication = authToken
                     chain.doFilter(request, response)
                 }
             } catch (e: TokenExpiredException) {
                 handleExceptions(e, response)
             }
         }
    }

    private fun handleExceptions(e: Exception, response: HttpServletResponse) {
        response.apply {
            val error = ErrorResponse(
                statusMessage = "Token Expired",
                statusCode = HttpServletResponse.SC_UNAUTHORIZED
            )

            status = HttpServletResponse.SC_UNAUTHORIZED
            contentType = "application/json"
            characterEncoding = "UTF-8"
            writer.write(ObjectMapper().writeValueAsString(error))
        }
    }
}