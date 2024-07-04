package com.portal.hrms.controller

import com.portal.hrms.dto.PermissionResponse
import com.portal.hrms.service.AccessControlService
import com.portal.hrms.utils.Role
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/acl/permission/user")
@PreAuthorize("hasRole('${Role.ADMIN}')")
class AccessControlControllerImpl(
    private val acs: AccessControlService
) : AccessControlController {

    @PatchMapping
    override fun addPermissionsToUser(
        @RequestParam("id") employeeId: Long,
        @RequestBody permissions: List<String>
    ): ResponseEntity<Any> {
        val (employee, addedPermissions) = acs.addPermissionsToUser(employeeId, permissions)
        val permissionResponse = PermissionResponse(
            statusMessage = "Permissions Added for ${employee.name}(${employee.empId})",
            permissions = addedPermissions,
            success = true
        )

        return ResponseEntity(permissionResponse, HttpStatus.OK)
    }

    @DeleteMapping
    override fun revokePermissions(
        @RequestParam("id") employeeId: Long,
        @RequestBody permissions: List<String>
    ): ResponseEntity<Any> {
        val (employee, deletedPermissions) = acs.revokePermissions(employeeId, permissions)

        val permissionResponse= PermissionResponse(
            statusMessage = "Permission(s) revoked for ${employee.name}",
            permissions = deletedPermissions,
            success = true
        )

        return ResponseEntity(permissionResponse, HttpStatus.OK)
    }
}


interface AccessControlController {
    fun addPermissionsToUser(employeeId: Long, permissions: List<String>): ResponseEntity<Any> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

    fun changeUserRole(employeeId: Long, newRole: String): ResponseEntity<Any> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

    fun revokePermissions(employeeId: Long, permissions: List<String>): ResponseEntity<Any> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }
}