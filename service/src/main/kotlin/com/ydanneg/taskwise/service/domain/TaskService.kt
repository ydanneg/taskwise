package com.ydanneg.taskwise.service.domain

import com.ydanneg.taskwise.model.Task
import com.ydanneg.taskwise.model.TaskPriority
import com.ydanneg.taskwise.model.TaskStatus
import com.ydanneg.taskwise.service.data.TaskEntity
import com.ydanneg.taskwise.service.data.TaskRepository
import com.ydanneg.taskwise.service.web.ServiceException.TaskNotFoundException
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Service
class TaskService(
    val taskRepository: TaskRepository
) {

    suspend fun getAllTasks(pageRequest: Pageable): Page<Task> =
        taskRepository.findAll()
            .drop(pageRequest.pageSize * pageRequest.pageNumber)
            .take(pageRequest.pageSize)
            .map { it.toModel() }
            .toList()
            .let { PageImpl(it, pageRequest, taskRepository.count()) }

    @Transactional(readOnly = true)
    suspend fun getTask(taskId: UUID): Task = getOrThrow(taskId).toModel()

    @Transactional
    suspend fun createTask(
        userId: String,
        title: String,
        priority: TaskPriority = TaskPriority.LOW,
        description: String? = null,
        dueDate: LocalDate? = null,
        assignedTo: String? = null
    ): Task {
        val entity = TaskEntity(
            title = title,
            description = description,
            status = TaskStatus.TODO,
            priority = priority,
            dueDate = dueDate,
            assignedTo = assignedTo,
            createdBy = userId
        )
        return taskRepository.save(entity).toModel()
    }

    private suspend fun getOrThrow(taskId: UUID): TaskEntity =
        taskRepository.findById(taskId) ?: throw TaskNotFoundException(taskId.toString())

    @Transactional
    suspend fun updateTaskStatus(taskId: UUID, status: TaskStatus): Task =
        getOrThrow(taskId)
            .copy(status = status)
            .let {
                if (status == TaskStatus.COMPLETED) it.copy(completedAt = Instant.now())
                else it.copy(completedAt = null)
            }
            .let { taskRepository.save(it) }
            .toModel()

    @Transactional
    suspend fun getUserAssignedTasks(userId: String, pageRequest: Pageable): Page<Task> =
        taskRepository.findByAssignedTo(userId)
            .drop(pageRequest.pageSize * pageRequest.pageNumber)
            .take(pageRequest.pageSize)
            .map { it.toModel() }
            .toList()
            .let { PageImpl(it, pageRequest, taskRepository.countByAssignedTo(userId)) }

    private fun TaskEntity.toModel() = Task(
        id = id.toString(),
        title = title,
        description = description,
        status = status,
        priority = priority,
        dueDate = dueDate,
        assignedTo = assignedTo,
        createdAt = createdAt!!,
        createdBy = createdBy,
        completedAt = completedAt
    )
}