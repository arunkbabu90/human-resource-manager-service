package com.portal.hrms.mapper

import com.portal.hrms.domain.Employee
import com.portal.hrms.dto.EmployeeRequest
import com.portal.hrms.dto.EmployeeResponse
import com.portal.hrms.dto.GetEmployeeResponse
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

//@Mapper(componentModel = "spring", uses = [LocationMapper::class])
@Mapper(componentModel = "spring")
interface EmployeeMapper {
    companion object {
        val INSTANCE: EmployeeMapper = Mappers.getMapper(EmployeeMapper::class.java)
    }

    fun employeeToEmployeeRequest(employee: Employee): EmployeeRequest

    fun employeeRequestToEmployee(employeeRequest: EmployeeRequest): Employee

    @Mapping(source = "empId", target = "employeeId")
    fun employeeToEmployeeResponse(employee: Employee): EmployeeResponse

    @Mapping(source = "empId", target = "employeeId")
    fun employeeToGetEmployeeResponse(employee: Employee): GetEmployeeResponse
}
