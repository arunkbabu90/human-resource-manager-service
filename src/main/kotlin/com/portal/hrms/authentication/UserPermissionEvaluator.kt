package com.portal.hrms.authentication

import com.portal.hrms.domain.Role
import com.portal.hrms.repository.PermissionRoleRepository
import com.portal.hrms.repository.UserPermissionRepository
import com.portal.hrms.repository.UserRepository
import com.portal.hrms.repository.UserRoleRepository
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.io.Serializable

@Component
class UserPermissionEvaluator(
    private val userRepository: UserRepository,
    private val userPermissionRepository: UserPermissionRepository,
    private val userRoleRepository: UserRoleRepository,
    private val permissionRoleRepository: PermissionRoleRepository
) : PermissionEvaluator {

    override fun hasPermission(authentication: Authentication?, targetDomainObject: Any?, permission: Any?): Boolean {
        return if (authentication != null) {
            val username = authentication.name
            val user = userRepository.findByUsername(username) ?: return false

            // Check individual granted permissions
            val userPermissions: List<String> = userPermissionRepository.findByUser(user).mapNotNull { it.permission?.name }
            if (permission is String && userPermissions.contains(permission.uppercase())) {
                return true
            }

            // Check role-based permissions
            val userRoles: List<Role> = userRoleRepository.findByUser(user).mapNotNull { it.role }
            val rolePermissions: List<String> = userRoles.flatMap { role ->
                permissionRoleRepository.findByRole(role).mapNotNull { it.permission?.name }
            }

            return permission is String && rolePermissions.contains(permission.uppercase())
        } else {
            false
        }
    }

    override fun hasPermission(
        authentication: Authentication?,
        targetId: Serializable?,
        targetType: String?,
        permission: Any?
    ): Boolean {
        // Domain Object level permission checks
        return false
    }

}