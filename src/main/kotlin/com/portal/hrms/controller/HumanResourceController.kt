package com.portal.hrms.controller

import com.portal.hrms.domain.Employee
import com.portal.hrms.domain.User
import com.portal.hrms.dto.EmployeeRequest
import com.portal.hrms.dto.GetEmployeeResponse
import com.portal.hrms.dto.UserRequest
import com.portal.hrms.exception.BadRequestException
import com.portal.hrms.exception.ResourceExistsException
import com.portal.hrms.mapper.EmployeeMapper
import com.portal.hrms.service.HumanResourceService
import com.portal.hrms.utils.READ
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/hrms/employee")
class HumanResourceControllerImpl(
    private val humanResourceService: HumanResourceService,
    private val mapper: EmployeeMapper,
    private val passwordEncoder: PasswordEncoder
) : HumanResourceController {

    @GetMapping("/user/create")
    @PreAuthorize("hasPermission(null, 'USER_CREATE')")
    fun userCreate(): ResponseEntity<Any> {
        return ResponseEntity("User Create", HttpStatus.OK)
    }

    @PreAuthorize("hasPermission(null, '${READ}')")
    @GetMapping("/all")
    override fun getAllEmployees(): List<GetEmployeeResponse> {
        val employees = humanResourceService.getAllEmployees()
        return employees.map { mapper.employeeToGetEmployeeResponse(it) }
    }


    @PostMapping
    override fun addEmployee(@RequestBody request: UserRequest): ResponseEntity<Any> {
        val encodedPassword = passwordEncoder.encode(request.password)
        val user = User(
            username = request.username,
            password = encodedPassword,
            isBlocked = false
        )

        val employee = Employee(
            name = request.name,
            designation = request.designation
        )

        try {
            humanResourceService.addEmployee(user, employee, request.role, request.additionalPermissions)
        } catch (e: DataIntegrityViolationException) {
            val message = e.message ?: ""
            if (message.contains("duplicate key value violates unique constraint", ignoreCase = true)) {
                throw ResourceExistsException("User ${user.username} already exists")
            } else {
                throw e
            }
        }

        return ResponseEntity("User created successfully", HttpStatus.CREATED)
    }


    @DeleteMapping
    override fun deleteEmployee(@RequestParam("id") employeeId: Long?): ResponseEntity<Any> {
        if (employeeId == null) {
            return ResponseEntity(
                BadRequestException("Id of the employee to be deleted is not specified"),
                HttpStatus.BAD_REQUEST
            )
        }
        val employee = humanResourceService.deleteEmployee(employeeId)
        val employeeResponse = mapper.employeeToEmployeeResponse(employee).apply {
            message = "Employee Deleted"
        }

        return ResponseEntity(employeeResponse, HttpStatus.OK)
    }


    @PutMapping
    override fun editEmployee(
        @RequestBody employeeRequest: EmployeeRequest,
        @RequestParam("id") employeeId: Long
    ): ResponseEntity<Any> {
        val employee = mapper.employeeRequestToEmployee(employeeRequest).apply {
            empId = employeeId
        }
        val editedEmployee = humanResourceService.editEmployee(employee)
        val employeeResponse = mapper.employeeToEmployeeResponse(editedEmployee).apply {
            message = "Employee Updated"
        }

        return ResponseEntity(employeeResponse, HttpStatus.OK)
    }
}

interface HumanResourceController {
    fun addEmployee(request: UserRequest): ResponseEntity<Any> = ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    fun getAllEmployees(): List<GetEmployeeResponse> = listOf()
    fun deleteEmployee(employeeId: Long?): ResponseEntity<Any> = ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    fun editEmployee(employeeRequest: EmployeeRequest, employeeId: Long): ResponseEntity<Any> = ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
}