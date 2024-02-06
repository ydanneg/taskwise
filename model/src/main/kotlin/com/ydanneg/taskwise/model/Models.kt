package com.ydanneg.taskwise.model

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate

@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String? = null,
    @Serializable(with = KLocalDateSerializer::class)
    val dueDate: LocalDate? = null,
    val priority: TaskPriority = TaskPriority.LOW,
    val assignedTo: String? = null,
)

@Serializable
data class UpdateTaskStatusRequest(
    val status: TaskStatus
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