package com.ydanneg.taskwise.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable
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
    @field:Size(min = 1, max = 255)
    @field:NotNull
    val title: String,
    @field:Size(min = 3, max = 16000)
    val description: String? = null,
    @Serializable(with = KLocalDateSerializer::class)
    val dueDate: LocalDate? = null,
    val priority: TaskPriority = TaskPriority.LOW,
    val assignedTo: String? = null,
)

@Serializable
data class UpdateTaskTitleRequest(
    @field:Size(min = 1, max = 255)
    @field:NotNull
    val title: String
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
    @Serializable(with = KLocalDateSerializer::class)
    val dueDate: LocalDate?
)

enum class TaskStatus {
    TODO, IN_PROGRESS, COMPLETED
}

enum class TaskPriority {
    LOW, MEDIUM, HIGH
}

@Serializable
data class Task(
    val id: String,
    val title: String,
    val description: String? = null,
    val status: TaskStatus = TaskStatus.TODO,
    val priority: TaskPriority = TaskPriority.LOW,
    @Serializable(with = KLocalDateSerializer::class)
    val dueDate: LocalDate? = null,
    val assignedTo: String? = null,
    @Serializable(with = KInstantSerializer::class)
    val createdAt: Instant,
    val createdBy: String,
    @Serializable(with = KInstantSerializer::class)
    val completedAt: Instant? = null
)