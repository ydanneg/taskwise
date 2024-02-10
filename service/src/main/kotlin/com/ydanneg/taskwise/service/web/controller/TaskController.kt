package com.ydanneg.taskwise.service.web.controller

import com.ydanneg.taskwise.model.Task
import com.ydanneg.taskwise.model.TaskListFilter
import com.ydanneg.taskwise.model.UpdateTaskAssigneeRequest
import com.ydanneg.taskwise.model.UpdateTaskDescriptionRequest
import com.ydanneg.taskwise.model.UpdateTaskDueDateRequest
import com.ydanneg.taskwise.model.UpdateTaskPriorityRequest
import com.ydanneg.taskwise.model.UpdateTaskStatusRequest
import com.ydanneg.taskwise.model.UpdateTaskTitleRequest
import com.ydanneg.taskwise.service.domain.TaskService
import com.ydanneg.taskwise.service.web.V1Constants
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
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
@Tag(name = "Tasks", description = "Task operations")
class TaskController(private val taskService: TaskService) {

    @GetMapping("/{taskId}")
    @ResponseStatus(OK)
    @Operation(summary = "Get a task by id")
    suspend fun getTask(@PathVariable taskId: String): Task =
        taskService.getTask(UUID.fromString(taskId))

    @PutMapping("/{taskId}/status")
    @ResponseStatus(OK)
    @Operation(summary = "Update the status of a task. Completed status adds a completion date.")
    suspend fun updateTaskStatus(
        @PathVariable taskId: String,
        @Valid @RequestBody request: UpdateTaskStatusRequest
    ): Task = taskService.updateTaskStatus(UUID.fromString(taskId), request.status)

    @PutMapping("/{taskId}/priority")
    @ResponseStatus(OK)
    @Operation(summary = "Update the priority of a task")
    suspend fun updateTaskPriority(
        @PathVariable taskId: String,
        @Valid @RequestBody request: UpdateTaskPriorityRequest
    ): Task = taskService.updateTaskPriority(UUID.fromString(taskId), request.priority)

    @PutMapping("/{taskId}/title")
    @ResponseStatus(OK)
    @Operation(summary = "Update the title of a task")
    suspend fun updateTaskTitle(
        @PathVariable taskId: String,
        @Valid @RequestBody request: UpdateTaskTitleRequest
    ): Task = taskService.updateTaskTitle(UUID.fromString(taskId), request.title)

    @PutMapping("/{taskId}/description")
    @ResponseStatus(OK)
    @Operation(summary = "Update the description of a task")
    suspend fun updateTaskDescription(
        @PathVariable taskId: String,
        @Valid @RequestBody request: UpdateTaskDescriptionRequest
    ): Task = taskService.updateTaskDescription(UUID.fromString(taskId), request.description)

    @PutMapping("/{taskId}/assignee")
    @ResponseStatus(OK)
    @Operation(summary = "Update the assignee of a task")
    suspend fun updateTaskAssignee(
        @PathVariable taskId: String,
        @Valid @RequestBody request: UpdateTaskAssigneeRequest
    ): Task = taskService.updateTaskAssignee(UUID.fromString(taskId), request.assignee)

    @PutMapping("/{taskId}/due-date")
    @ResponseStatus(OK)
    @Operation(summary = "Update the due date of a task")
    suspend fun updateTaskDueDate(
        @PathVariable taskId: String,
        @Valid @RequestBody request: UpdateTaskDueDateRequest
    ): Task = taskService.updateTaskDueDate(UUID.fromString(taskId), request.dueDate)


    @GetMapping
    @ResponseStatus(OK)
    @Operation(summary = "Get all tasks with optional filtering and pagination")
    suspend fun getAllTasks(
        @ParameterObject @Valid filter: TaskListFilter = TaskListFilter(),
        @ParameterObject @PageableDefault(size = V1Constants.DEFAULT_PAGE_SIZE, page = 0) pageRequest: Pageable
    ): Page<Task> = taskService.getAllTasks(filter, pageRequest)

    @DeleteMapping("/{taskId}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Delete a task by id")
    suspend fun deleteTask(@PathVariable taskId: UUID) = taskService.deleteTask(taskId)
}