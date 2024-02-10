@file:UseSerializers(KLocalDateSerializer::class, KInstantSerializer::class)

package com.ydanneg.taskwise.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant
import java.time.LocalDate

@Serializable
data class ErrorDto(
    val errorCode: String = UNKNOWN_ERROR_CODE,
    val message: String = UNKNOWN_ERROR_MSG,
    val params: Map<String, String> = mapOf(),
) {
    companion object {
        const val UNKNOWN_ERROR_CODE = "UNKNOWN"
        const val UNKNOWN_ERROR_MSG = "Unknown error"

        const val NOT_FOUND_ERROR_CODE = "NOT_FOUND"
        const val NOT_FOUND_ERROR_MSG = "Not found"

        const val BAD_REQUEST_ERROR_CODE = "BAD_REQUEST"
        const val BAD_REQUEST_ERROR_MSG = "Bad request"
    }
}

@Serializable
data class CreateTaskRequest(
    @field:Size(min = 1, max = 255) val title: String,
    @field:Size(min = 3, max = 16000)
    val description: String? = null,
    val dueDate: LocalDate? = null,
    val priority: TaskPriority = TaskPriority.LOW,
    @field:Size(min = 1, max = 255) val assignee: String? = null,
)

@Serializable
data class UpdateTaskTitleRequest(
    @field:Size(min = 1, max = 255) val title: String
)

@Serializable
data class UpdateTaskStatusRequest(val status: TaskStatus)

@Serializable
data class UpdateTaskPriorityRequest(val priority: TaskPriority)

@Serializable
data class UpdateTaskDescriptionRequest(val description: String?)

@Serializable
data class UpdateTaskAssigneeRequest(val assignee: String?)

@Serializable
data class UpdateTaskDueDateRequest(
    val dueDate: LocalDate?
)

enum class TaskStatus {
    TODO, IN_PROGRESS, DONE
}

enum class TaskPriority {
    LOW, MEDIUM, HIGH
}

@Serializable
data class TaskListFilter(
    val status: Set<TaskStatus> = setOf(),
    val priority: Set<TaskPriority> = setOf(),
    val assignee: Set<@NotBlank String> = setOf(),
    val createdBy: Set<@NotBlank String> = setOf(),
)

@Serializable
data class Task(
    val id: String,
    val title: String,
    val description: String? = null,
    val status: TaskStatus = TaskStatus.TODO,
    val priority: TaskPriority = TaskPriority.LOW,
    val dueDate: LocalDate? = null,
    val assignee: String? = null,
    val createdAt: Instant,
    val createdBy: String,
    val version: Long,
    val completedAt: Instant? = null,
)

@Serializable
data class CompletionReportResponse(
    val content: List<CompletionReport>
)

@Serializable
data class CompletionReport(
    val assignee: String,
    val total: Long,
    val avgTime: Long
)
