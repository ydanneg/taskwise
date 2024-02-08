package com.ydanneg.taskwise.service.data

import com.ydanneg.taskwise.model.TaskPriority
import com.ydanneg.taskwise.model.TaskStatus
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.time.LocalDate
import java.util.UUID


@Table("tasks")
data class TaskEntity(
    @Id
    val id: UUID? = null,
    val title: String? = null,
    val description: String? = null,
    val status: TaskStatus? = null,
    val priority: TaskPriority? = null,
    val dueDate: LocalDate? = null,
    val assignedTo: String? = null,
    val createdBy: String? = null,
    @CreatedDate
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null,
    val completedAt: Instant? = null
)