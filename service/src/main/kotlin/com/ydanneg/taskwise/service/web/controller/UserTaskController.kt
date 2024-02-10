package com.ydanneg.taskwise.service.web.controller

import com.ydanneg.taskwise.model.CreateTaskRequest
import com.ydanneg.taskwise.model.Task
import com.ydanneg.taskwise.service.domain.TaskService
import com.ydanneg.taskwise.service.web.V1Constants
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(V1Constants.USER_TASKS)
@Tag(name = "User Tasks", description = "User Task operations")
class UserTaskController(private val taskService: TaskService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a task by a user")
    suspend fun createTask(
        @PathVariable userId: String,
        @Valid @RequestBody request: CreateTaskRequest
    ): Task = taskService.createTask(
        userId = userId,
        title = request.title,
        description = request.description,
        priority = request.priority,
        dueDate = request.dueDate,
        assignee = request.assignee
    )
}
