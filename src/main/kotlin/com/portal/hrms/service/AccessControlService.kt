package com.portal.hrms.service

import com.portal.hrms.domain.Employee
import com.portal.hrms.domain.Permission
import com.portal.hrms.domain.UserPermission
import com.portal.hrms.exception.ResourceNotFoundException
import com.portal.hrms.repository.EmployeeRepository
import com.portal.hrms.repository.PermissionRepository
import com.portal.hrms.repository.UserPermissionRepository
import com.portal.hrms.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrElse

@Service
class AccessControlServiceImpl(
    private val userPermissionRepository: UserPermissionRepository,
    private val userRepository: UserRepository,
    private val employeeRepository: EmployeeRepository,
    private val permissionRepository: PermissionRepository
) : AccessControlService {

    @Transactional(rollbackFor = [Exception::class])
    override fun addPermissionsToUser(employeeId: Long, permissions: List<String>): Pair<Employee, List<String>> {
        val employee = employeeRepository.findById(employeeId).getOrElse {
            throw ResourceNotFoundException("Employee with id $employeeId not Found")
        }

        val user = userRepository.findByEmployee(employee)
            ?: throw ResourceNotFoundException("User not found")

        permissions.forEach { permission ->
            if (permission.isNotBlank()) {
                val validPermissions = permissionRepository.findAll()
                val validPermissionsString = validPermissions.joinToString(", ") { it.name.uppercase() }
                val permissionEntity: Permission? = validPermissions.find { it.name == permission.uppercase() }

                if (permissionEntity != null) {
                    userPermissionRepository.save(UserPermission(user = user, permission = permissionEntity))
                } else {
                    throw ResourceNotFoundException("Permission $permission is not valid, Valid permissions are $validPermissionsString")
                }
            }
        }

        return employee to permissions
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun revokePermissions(employeeId: Long, permissionsToRevoke: List<String>): Pair<Employee, List<String>> {
        val deletedPermissions = mutableListOf<String>()

        val employee = employeeRepository.findById(employeeId).getOrElse {
            throw ResourceNotFoundException("Employee not found")
        }

        val user = userRepository.findByEmployee(employee)
            ?: throw ResourceNotFoundException("User not found")

        val grantedPermissions = userPermissionRepository.findByUser(user)
        val deletablePermissions = grantedPermissions.filter { grantedPermission ->
            permissionsToRevoke.any { it.equals(grantedPermission.permission?.name, ignoreCase = true) }
        }

        deletablePermissions.forEach { deletedPermission ->
            userPermissionRepository.delete(deletedPermission)
            deletedPermission.permission?.name?.takeIf { it.isNotBlank() }?.let {
                deletedPermissions.add(it)
            }
        }

        return employee to deletedPermissions
    }
}

interface AccessControlService {
    fun addPermissionsToUser(employeeId: Long, permissions: List<String>): Pair<Employee, List<String>> = (Employee() to listOf())
    fun changeUserRole(employeeId: Long, newRole: String): Pair<Employee, String> = (Employee() to "")
    fun revokePermissions(employeeId: Long, permissionsToRevoke: List<String>): Pair<Employee, List<String>> = (Employee() to listOf())
}