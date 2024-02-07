package com.ydanneg.taskwise.service.web.controller

import com.ydanneg.taskwise.model.Task
import com.ydanneg.taskwise.model.UpdateTaskStatusRequest
import com.ydanneg.taskwise.service.domain.TaskService
import com.ydanneg.taskwise.service.web.V1Constants
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping(V1Constants.TASKS)
class TaskController(private val taskService: TaskService) {

    @GetMapping("/{taskId}")
    @ResponseStatus(OK)
    suspend fun getTask(@PathVariable taskId: String): Task =
        taskService.getTask(UUID.fromString(taskId))

    @PutMapping("/{taskId}/status")
    @ResponseStatus(OK)
    suspend fun updateTaskStatus(
        @PathVariable taskId: String,
        @Valid @RequestBody request: UpdateTaskStatusRequest
    ): Task = taskService.updateTaskStatus(UUID.fromString(taskId), request.status)

    @GetMapping
    suspend fun getAllTasks(@ParameterObject @PageableDefault(size = V1Constants.DEFAULT_PAGE_SIZE, page = 0) pageRequest: Pageable): Page<Task> =
        taskService.getAllTasks(pageRequest)
}