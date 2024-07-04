package com.portal.hrms.utils

import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission

class HRMSPermission : BasePermission {
    private constructor(mask: Int) : super(mask)
    private constructor(mask: Int, code: Char) : super(mask, code)

    companion object {
        val FORWARD: Permission = HRMSPermission(1 shl 3, 'F')
        val EDIT: Permission = HRMSPermission(1 shl 4, 'E')
        val REPORT: Permission = HRMSPermission(1 shl 5, 'O')
    }
}