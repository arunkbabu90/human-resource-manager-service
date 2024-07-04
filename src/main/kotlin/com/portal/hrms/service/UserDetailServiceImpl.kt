package com.portal.hrms.service

import com.portal.hrms.exception.ResourceNotFoundException
import com.portal.hrms.repository.UserPermissionRepository
import com.portal.hrms.repository.UserRepository
import com.portal.hrms.repository.UserRoleRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailServiceImpl(
    private val userRepository: UserRepository,
    private val userRoleRepository: UserRoleRepository,
    private val userPermissionRepository: UserPermissionRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails? {
        val uName = username.toString()
        val user = userRepository.findByUsername(uName)
            ?: throw ResourceNotFoundException("User with username $uName not found")

        val roles: List<SimpleGrantedAuthority> =
            userRoleRepository.findByUser(user).map { SimpleGrantedAuthority(it.role?.name) }
        val permissions: List<SimpleGrantedAuthority> =
            userPermissionRepository.findByUser(user).map { SimpleGrantedAuthority(it.permission?.name) }
        val authorities: List<SimpleGrantedAuthority> = roles + permissions

        return User(
            user.username, user.password, !user.isBlocked, true, true,
            true, authorities
        )
    }
}