package com.portal.hrms.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GetEmployeeResponse(
    var employeeId: Long = 0,
    var name: String,
    var designation: String
)