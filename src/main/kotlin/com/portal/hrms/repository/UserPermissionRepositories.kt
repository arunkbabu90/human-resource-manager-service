package com.portal.hrms.repository

import com.portal.hrms.domain.*
import org.springframework.data.jpa.repository.JpaRepository


interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun findByEmployee(employee: Employee): User?
}

interface RoleRepository : JpaRepository<Role, Long>

interface UserRoleRepository : JpaRepository<UserRole, Long> {
    fun findByUser(user: User): List<UserRole>
}

interface PermissionRepository : JpaRepository<Permission, Long>

interface UserPermissionRepository : JpaRepository<UserPermission, Long> {
    fun findByUser(user: User): List<UserPermission>
}

interface PermissionRoleRepository : JpaRepository<RolePermission, Long> {
    fun findByRole(role: Role): List<RolePermission>
}
