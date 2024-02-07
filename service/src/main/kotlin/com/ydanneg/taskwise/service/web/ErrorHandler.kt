package com.ydanneg.taskwise.service.web

import com.ydanneg.taskwise.model.ErrorDto
import com.ydanneg.taskwise.service.web.ServiceException.TaskNotFoundException
import com.ydanneg.taskwise.util.getLogger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException

@RestControllerAdvice
class ErrorHandler {

    private val log = getLogger()

    companion object {
        const val ERROR_LOG_TITLE = "ErrorHandler exception:"
        const val UNKNOWN_ERROR: String = "Unknown error"
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun onWebExchangeBindException(exception: WebExchangeBindException): ResponseEntity<ErrorDto> {
        val message = exception.bindingResult.allErrors.first()?.let {
            if (it is FieldError) {
                "${it.field}: ${it.defaultMessage}"
            } else {
                "${it.code}: ${it.defaultMessage}"
            }
        } ?: exception.message
        return logAndBuildResponseEntity(IllegalArgumentException(message), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ServiceException::class)
    fun onServiceException(exception: ServiceException): ResponseEntity<ErrorDto> =
        when (exception) {
            is TaskNotFoundException -> logAndBuildResponseEntity(exception, HttpStatus.NOT_FOUND)
        }

    @ExceptionHandler(java.lang.Exception::class, Exception::class)
    fun onException(exception: Exception): ResponseEntity<ErrorDto> =
        logAndBuildResponseEntity(exception, HttpStatus.INTERNAL_SERVER_ERROR)

    private fun logAndBuildResponseEntity(exception: Exception, status: HttpStatus): ResponseEntity<ErrorDto> {
        if (status.is5xxServerError) {
            log.error(ERROR_LOG_TITLE, exception)
        } else {
            log.warn(ERROR_LOG_TITLE, exception)
        }

        return when (exception) {
            is IllegalArgumentException -> responseEntity(buildErrorDto(ErrorDto.BAD_REQUEST_ERROR_CODE, exception.message ?: ErrorDto.BAD_REQUEST_ERROR_MSG), status)
            is TaskNotFoundException -> responseEntity(buildErrorDto(exception.errorCode, exception.message), status)
            else -> responseEntity(buildErrorDto(ErrorDto.UNKNOWN_ERROR_CODE, exception.message ?: ErrorDto.UNKNOWN_ERROR_MSG), status)
        }
    }

    private fun responseEntity(error: ErrorDto, status: HttpStatus) =
        ResponseEntity.status(status).body(error)

    private fun buildErrorDto(errorCode: String, message: String) = ErrorDto(errorCode = errorCode, message = message)
}
