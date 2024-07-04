package com.portal.hrms.domain

import jakarta.persistence.*

@Entity
data class Employee(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var empId: Long = 0,
    var name: String = "",
    var designation: String = "",

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id")
    var user: User? = null
) {
    override fun toString(): String {
        return "Employee(empId=$empId, name='$name', designation='$designation')"
    }

    fun isBlank() = empId == 0L && name.isBlank() && designation.isBlank()
    fun isNotBlank() = !isBlank()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Employee

        if (empId != other.empId) return false
        if (name != other.name) return false
        if (designation != other.designation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = empId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + designation.hashCode()
        return result
    }
}
