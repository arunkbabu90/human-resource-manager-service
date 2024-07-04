package com.portal.hrms.domain

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false, unique = true)
    val username: String = "",
    var password: String = "",
    val isBlocked: Boolean = false,

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var employee: Employee? = null
) {
    override fun toString(): String {
        return "User(id=$id, username='$username', password='$password', isBlocked=$isBlocked)"
    }
}

@Entity
@Table(name = "roles")
data class Role(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String = ""
)

@Entity
@Table(name = "user_roles")
data class UserRole(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne val user: User? = null,
    @ManyToOne val role: Role? = null
)

@Entity
@Table(name = "permissions")
data class Permission(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String = ""
)

@Entity
@Table(name = "user_permissions")
data class UserPermission(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne val user: User? = null,
    @ManyToOne val permission: Permission? = null
)

@Entity
@Table(name = "role_permissions")
data class RolePermission(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne val role: Role? = null,
    @ManyToOne val permission: Permission? = null
)
