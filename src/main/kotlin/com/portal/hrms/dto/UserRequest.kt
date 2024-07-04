package com.portal.hrms.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UserRequest(
    var username: String,
    var password: String,
    var name: String,
    var designation: String,
    var role: String,
    var additionalPermissions: List<String>
)
