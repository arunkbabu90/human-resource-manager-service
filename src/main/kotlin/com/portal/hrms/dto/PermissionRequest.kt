package com.portal.hrms.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PermissionRequest(
    val className: String,
    val sid: String,
    val permission: String,
    val isRole: Boolean
)