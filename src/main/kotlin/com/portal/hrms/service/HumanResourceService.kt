package com.portal.hrms.service

import com.portal.hrms.domain.Employee
import com.portal.hrms.domain.User
import com.portal.hrms.domain.UserRole
import com.portal.hrms.exception.BadRequestException
import com.portal.hrms.exception.NoUpdatesException
import com.portal.hrms.exception.ResourceNotFoundException
import com.portal.hrms.repository.*
import com.portal.hrms.utils.CREATE
import com.portal.hrms.utils.DELETE
import com.portal.hrms.utils.Role
import com.portal.hrms.utils.UPDATE
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrElse


@Service
class HumanResourceServiceImpl(
    private val employeeRepository: EmployeeRepository,
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val userRoleRepository: UserRoleRepository,
    private val userPermissionRepository: UserPermissionRepository,
    private val acs: AccessControlService
) : HumanResourceService {

    override fun getAllEmployees(): List<Employee> = employeeRepository.findAllByOrderByEmpIdAsc()


    @PreAuthorize("(hasRole('${Role.HR}') or hasRole('${Role.ADMIN}')) " +
            "or hasPermission(null, '$CREATE')")
    @Transactional(rollbackFor = [Exception::class])
    override fun addEmployee(
        user: User,
        employee: Employee,
        role: String,
        additionalPermissions: List<String>
    ) {
        val savedUser = userRepository.save(user)
        employee.user = savedUser
        val savedEmployee = employeeRepository.save(employee)

        // Map the User with the requested Role
        if (role.isNotBlank()) {
            val roles: MutableList<com.portal.hrms.domain.Role> = roleRepository.findAll()
            val rolesString = roles.joinToString(", ") { it.name }
            val roleEntity = roles.find { it.name == role.uppercase() }
            if (roleEntity != null) {
                userRoleRepository.save(UserRole(user = savedUser, role = roleEntity))
            } else {
                throw ResourceNotFoundException("Role $role is not valid. Valid roles are: $rolesString")
            }
        }

        acs.addPermissionsToUser(savedEmployee.empId, additionalPermissions)
    }


    @PreAuthorize("(hasRole('${Role.HR}') or hasRole('${Role.ADMIN}')) " +
            "or hasPermission(null, '$DELETE')")
    @Transactional(rollbackFor = [Exception::class])
    override fun deleteEmployee(employeeId: Long): Employee {
        // Find User id by empId
        val employee = employeeRepository.findById(employeeId).getOrElse {
            throw ResourceNotFoundException("Employee not found")
        }

        val user = userRepository.findByEmployee(employee)
            ?: throw ResourceNotFoundException("User not found")

        // Delete the additional permission granted to the user
        userPermissionRepository.findByUser(user).forEach { userPermission ->
            userPermissionRepository.delete(userPermission)
        }

        // Delete the roles granted to the user
        userRoleRepository.findByUser(user).forEach { userRole ->
            userRoleRepository.delete(userRole)
        }

        // Delete the Employee
        employeeRepository.deleteById(employeeId)

        // Delete the User
        userRepository.delete(user)

        return employee
    }


    @PreAuthorize("(hasRole('${Role.HR}') or hasRole('${Role.ADMIN}')) " +
            "or hasPermission(null, '$UPDATE')")
    @Transactional(rollbackFor = [Exception::class])
    override fun editEmployee(updatedEmployee: Employee): Employee {
        if (updatedEmployee.isBlank()) {
            throw BadRequestException("No Data to perform Update")
        }

        val employee = employeeRepository.findById(updatedEmployee.empId).getOrElse {
            throw ResourceNotFoundException("No employee found with given id ${updatedEmployee.empId}")
        }

        if (updatedEmployee == employee) {
            throw NoUpdatesException("There are no updates to perform")
        }

        employee.apply {
            name = updatedEmployee.name
            designation = updatedEmployee.designation
        }

        return employeeRepository.save(employee)
    }
}

interface HumanResourceService {
    fun addEmployee(user: User, employee: Employee, role: String, additionalPermissions: List<String>)
    fun getAllEmployees(): List<Employee> = listOf()
    fun deleteEmployee(employeeId: Long) = Employee(0, "", "")
    fun editEmployee(updatedEmployee: Employee) = Employee(0, "", "")
}