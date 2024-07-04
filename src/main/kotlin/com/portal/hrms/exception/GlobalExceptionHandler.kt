package com.portal.hrms.exception

import com.auth0.jwt.exceptions.TokenExpiredException
import com.portal.hrms.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.acls.model.NotFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception, request: WebRequest) =
        ResponseEntity(
            ErrorResponse(
                statusMessage = "Internal Server Error",
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value()
            ), HttpStatus.INTERNAL_SERVER_ERROR
        )

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleProductNotFound(e: ResourceNotFoundException, request: WebRequest) =
        ResponseEntity(
            ErrorResponse(
                statusMessage = e.message ?: "No Products Found",
                statusCode = HttpStatus.NOT_FOUND.value()
            ), HttpStatus.NOT_FOUND
        )

    @ExceptionHandler(TokenExpiredException::class)
    fun handleTokenExpired(e: TokenExpiredException, request: WebRequest) =
        ResponseEntity(
            ErrorResponse(
                statusMessage = "Token Expired",
                statusCode = HttpStatus.UNAUTHORIZED.value()
            ), HttpStatus.UNAUTHORIZED
        )

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(e: AccessDeniedException, request: WebRequest) =
        ResponseEntity(
            ErrorResponse(
                statusMessage = e.message ?: "Access Denied! You don't have permission to perform this operation",
                statusCode = HttpStatus.UNAUTHORIZED.value()
            ), HttpStatus.UNAUTHORIZED
        )

    @ExceptionHandler(BadRequestException::class)
    fun handleBlankDataException(e: BadRequestException, request: WebRequest) =
        ResponseEntity(
            ErrorResponse(
                statusMessage = e.message ?: "Bad Request",
                statusCode = HttpStatus.BAD_REQUEST.value()
            ), HttpStatus.BAD_REQUEST
        )

    @ExceptionHandler(NoUpdatesException::class)
    fun handleNoUpdatesException(e: NoUpdatesException, request: WebRequest) =
        ResponseEntity(
            ErrorResponse(
                statusMessage = e.message ?: "No Updates found",
                statusCode = HttpStatus.ACCEPTED.value()
            ), HttpStatus.ACCEPTED
        )

    @ExceptionHandler(ResourceExistsException::class)
    fun handleResourceExistsException(e: ResourceExistsException, request: WebRequest) =
        ResponseEntity(
            ErrorResponse(
                statusMessage = e.message ?: "Resource already exists",
                statusCode = HttpStatus.CONFLICT.value()
            ), HttpStatus.CONFLICT
        )

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(e: NotFoundException, request: WebRequest) =
        ResponseEntity(
            ErrorResponse(
                statusMessage = e.message ?: "ACL entry not found",
                statusCode = HttpStatus.NOT_FOUND.value()
            ), HttpStatus.NOT_FOUND
        )

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException, request: WebRequest) =
        ResponseEntity(
            ErrorResponse(
                statusMessage = e.message ?: "Bad Request",
                statusCode = HttpStatus.BAD_REQUEST.value()
            ), HttpStatus.BAD_REQUEST
        )

    @ExceptionHandler(CreateResourceException::class)
    fun handleCreateResourceException(e: CreateResourceException, request: WebRequest) =
        ResponseEntity(
            ErrorResponse(
                statusMessage = e.message ?: "Failed to create the resource",
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value()
            ), HttpStatus.INTERNAL_SERVER_ERROR
        )

    @ExceptionHandler(DataIntegrityException::class)
    fun handleDataIntegrityException(e: DataIntegrityException, request: WebRequest) =
        ResponseEntity(
            ErrorResponse(
                statusMessage = e.message ?: "Data Integrity Error",
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value()
            ), HttpStatus.INTERNAL_SERVER_ERROR
        )
}