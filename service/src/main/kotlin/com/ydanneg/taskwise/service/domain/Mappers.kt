package com.ydanneg.taskwise.service.domain

import com.ydanneg.taskwise.model.Task
import com.ydanneg.taskwise.service.data.TaskEntity


fun TaskEntity.toModel() = Task(
    id = id.toString(),
    title = title,
    description = description,
    status = status,
    priority = priority,
    dueDate = dueDate,
    assignee = assignee,
    createdAt = createdAt!!,
    createdBy = createdBy,
    completedAt = completedAt,
    version = version!!
)