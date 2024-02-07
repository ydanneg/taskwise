package com.ydanneg.taskwise.service.web.controller

import com.ydanneg.taskwise.model.CreateTaskRequest
import com.ydanneg.taskwise.model.Task
import com.ydanneg.taskwise.service.domain.TaskService
import com.ydanneg.taskwise.service.web.V1Constants
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(V1Constants.USER_TASKS)
class UserTaskController(private val taskService: TaskService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createTask(
        @PathVariable userId: String,
        @Valid @RequestBody request: CreateTaskRequest
    ): Task = taskService.createTask(
        userId = userId,
        title = request.title,
        description = request.description,
        priority = request.priority,
        dueDate = request.dueDate,
        assignedTo = request.assignedTo
    )

    @GetMapping
    suspend fun getUserAssignedTasks(
        @PathVariable userId: String,
        @ParameterObject @PageableDefault(size = V1Constants.DEFAULT_PAGE_SIZE, page = 0) pageRequest: Pageable
    ): Page<Task> =
        taskService.getUserAssignedTasks(userId, pageRequest)
}
