package com.ydanneg.taskwise.service.web

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class TaskNotFoundException(val taskId: String) : Throwable()