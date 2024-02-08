package com.ydanneg.taskwise.service.data

import com.ydanneg.taskwise.model.TaskPriority
import com.ydanneg.taskwise.model.TaskStatus
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.LocalDate
import java.util.UUID


@Document("tasks")
data class TaskEntity(
    @Id
    val id: UUID? = null,
    val title: String,
    val description: String? = null,
    val status: TaskStatus = TaskStatus.TODO,
    val priority: TaskPriority = TaskPriority.LOW,
    val dueDate: LocalDate? = null,
    val assignedTo: String? = null,
    val createdBy: String,
    val createdAt: Instant? = Instant.now(),
    val updatedAt: Instant? = null,
    val completedAt: Instant? = null
)