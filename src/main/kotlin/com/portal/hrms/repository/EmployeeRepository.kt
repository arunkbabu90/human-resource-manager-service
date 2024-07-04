package com.portal.hrms.repository

import com.portal.hrms.domain.Employee
import org.springframework.data.jpa.repository.JpaRepository

interface EmployeeRepository : JpaRepository<Employee, Long> {
    fun findAllByOrderByEmpIdAsc(): List<Employee>
}