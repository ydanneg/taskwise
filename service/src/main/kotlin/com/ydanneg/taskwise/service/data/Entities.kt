package com.ydanneg.taskwise.service.data

import com.ydanneg.taskwise.model.TaskPriority
import com.ydanneg.taskwise.model.TaskStatus
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.index.TextIndexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.LocalDate
import java.util.UUID


@Document("tasks")
data class TaskEntity(
    @Id
    val id: UUID,
    @TextIndexed
    val title: String,
    @TextIndexed
    val description: String? = null,
    @Indexed
    val status: TaskStatus = TaskStatus.TODO,
    @Indexed
    val priority: TaskPriority = TaskPriority.LOW,
    @Indexed
    val dueDate: LocalDate? = null,
    @Indexed
    val assignee: String? = null,
    @Indexed
    val createdBy: String,
    @CreatedDate
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null,
    @Indexed
    val completedAt: Instant? = null,
    @Version
    val version: Long? = null
)