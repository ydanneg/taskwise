package com.ydanneg.taskwise.service.web.controller

import com.ydanneg.taskwise.model.Task
import com.ydanneg.taskwise.model.UpdateTaskAssigneeRequest
import com.ydanneg.taskwise.model.UpdateTaskDescriptionRequest
import com.ydanneg.taskwise.model.UpdateTaskDueDateRequest
import com.ydanneg.taskwise.model.UpdateTaskPriorityRequest
import com.ydanneg.taskwise.model.UpdateTaskStatusRequest
import com.ydanneg.taskwise.model.UpdateTaskTitleRequest
import com.ydanneg.taskwise.service.domain.TaskService
import com.ydanneg.taskwise.service.web.V1Constants
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.DeleteMapping
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

    @PutMapping("/{taskId}/priority")
    @ResponseStatus(OK)
    suspend fun updateTaskPriority(
        @PathVariable taskId: String,
        @Valid @RequestBody request: UpdateTaskPriorityRequest
    ): Task = taskService.updateTaskPriority(UUID.fromString(taskId), request.priority)

    @PutMapping("/{taskId}/title")
    @ResponseStatus(OK)
    suspend fun updateTaskTitle(
        @PathVariable taskId: String,
        @Valid @RequestBody request: UpdateTaskTitleRequest
    ): Task = taskService.updateTaskTitle(UUID.fromString(taskId), request.title)

    @PutMapping("/{taskId}/description")
    @ResponseStatus(OK)
    suspend fun updateTaskDescription(
        @PathVariable taskId: String,
        @Valid @RequestBody request: UpdateTaskDescriptionRequest
    ): Task = taskService.updateTaskDescription(UUID.fromString(taskId), request.description)

    @PutMapping("/{taskId}/assignee")
    @ResponseStatus(OK)
    suspend fun updateTaskAssignee(
        @PathVariable taskId: String,
        @Valid @RequestBody request: UpdateTaskAssigneeRequest
    ): Task = taskService.updateTaskAssignee(UUID.fromString(taskId), request.assignee)

    @PutMapping("/{taskId}/due-date")
    @ResponseStatus(OK)
    suspend fun updateTaskDueDate(
        @PathVariable taskId: String,
        @Valid @RequestBody request: UpdateTaskDueDateRequest
    ): Task = taskService.updateTaskDueDate(UUID.fromString(taskId), request.dueDate)


    @GetMapping
    @ResponseStatus(OK)
    suspend fun getAllTasks(
        @ParameterObject @PageableDefault(size = V1Constants.DEFAULT_PAGE_SIZE, page = 0) pageRequest: Pageable
    ): Page<Task> = taskService.getAllTasks(pageRequest)

    @DeleteMapping("/{taskId}")
    @ResponseStatus(NO_CONTENT)
    suspend fun deleteTask(@PathVariable taskId: UUID) = taskService.deleteTask(taskId)
}