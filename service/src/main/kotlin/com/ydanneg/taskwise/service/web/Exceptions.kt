package com.ydanneg.taskwise.service.web

import com.ydanneg.taskwise.model.ErrorDto

sealed class ServiceException(val errorCode: String, override val message: String) : RuntimeException(message) {
    class TaskNotFoundException(taskId: String) : ServiceException(ErrorDto.NOT_FOUND_ERROR_CODE, "Task with id $taskId not found")

}

