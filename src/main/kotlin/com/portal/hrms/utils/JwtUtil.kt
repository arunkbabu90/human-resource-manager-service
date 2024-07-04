package com.portal.hrms.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import com.portal.hrms.domain.AuthToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.time.Instant
import java.util.*
import kotlin.jvm.Throws

@Component
class JwtUtil {
    private val secret = "V4kOsup0GwpnQ2zavbmaHQA+iti7WOPtzoo4VzPzEAg="
    private val algorithm = Algorithm.HMAC256(secret)
    private val expiryInMinutes = 1200

    fun extractUsernameFromToken(token: String): String = JWT.decode(token).subject

    fun generateToken(userDetails: UserDetails): AuthToken {
        val roles: MutableCollection<out GrantedAuthority> = userDetails.authorities
        val rolesString: String = roles.joinToString(",") { it.authority }

        val createdAtDate = Instant.now()
        val expiryDate = Instant.now().plusSeconds((expiryInMinutes * 60).toLong())

        val jwtToken: String = JWT.create()
            .withSubject(userDetails.username)
            .withIssuedAt(createdAtDate)
            .withNotBefore(createdAtDate)
            .withExpiresAt(expiryDate)
            .withClaim("roles", rolesString)
            .sign(algorithm)

        return AuthToken(
            username = userDetails.username,
            authorities = roles,
            token = jwtToken,
            createdAt = createdAtDate.toEpochMilli(),
            expiresAt = expiryDate.toEpochMilli()
        )
    }

    @Throws(TokenExpiredException::class)
    fun validateToken(token: String): AuthToken {
        val verifier: JWTVerifier = JWT.require(algorithm).build()
        val jwt: DecodedJWT = verifier.verify(token)

        val rolesClaim = jwt.getClaim("roles")
        val rolesString = rolesClaim.asString()

        val roles: List<GrantedAuthority> = rolesString
            ?.split(",")
            ?.map { SimpleGrantedAuthority(it) }
            ?: emptyList()

        return AuthToken(
            username = jwt.subject,
            authorities = roles,
            token,
            jwt.expiresAt.time,
            jwt.issuedAt.time
        )
    }

    /**
     * Run this code individually to obtain the Secret key for testing
     * @return String The Secret key
     */
    fun generateSecretKey(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }
}