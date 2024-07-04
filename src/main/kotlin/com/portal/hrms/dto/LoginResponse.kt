package com.portal.hrms.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class LoginResponse(
    val username: String,
    val authorities: List<String>,
    val bearerToken: String,
    val createdAt: Long,
    val expiresAt: Long
)
