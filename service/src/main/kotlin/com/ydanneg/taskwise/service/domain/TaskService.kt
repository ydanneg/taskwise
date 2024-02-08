package com.ydanneg.taskwise.service.domain

import com.ydanneg.taskwise.model.Task
import com.ydanneg.taskwise.model.TaskPriority
import com.ydanneg.taskwise.model.TaskStatus
import com.ydanneg.taskwise.service.data.TaskEntity
import com.ydanneg.taskwise.service.web.ServiceException.TaskNotFoundException
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Service
@Transactional(readOnly = true)
class TaskService(val mongoTemplate: ReactiveMongoTemplate) {

    suspend fun getAllTasks(pageRequest: Pageable): Page<Task> {
        val query = Query()
        return mongoTemplate.find(query.with(pageRequest), TaskEntity::class.java)
            .map { it.toModel() }
            .asFlow()
            .toList()
            .let { PageImpl(it, pageRequest, mongoTemplate.count(query, TaskEntity::class.java).awaitSingle()) }
    }

    suspend fun getTask(taskId: UUID): Task = getOrThrow(taskId).toModel()

    @Transactional
    suspend fun deleteTask(taskId: UUID) {
        mongoTemplate.remove(getOrThrow(taskId)).awaitSingle()
    }

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
            id = UUID.randomUUID(),
            title = title,
            description = description,
            status = TaskStatus.TODO,
            priority = priority,
            dueDate = dueDate,
            assignedTo = assignedTo,
            createdBy = userId
        )
        return mongoTemplate.save(entity).awaitSingle().toModel()
    }

    @Transactional
    suspend fun updateTaskStatus(taskId: UUID, status: TaskStatus): Task =
        updateTask(taskId) {
            copy(status = status).let {
                if (status == TaskStatus.COMPLETED) it.copy(completedAt = Instant.now())
                else it.copy(completedAt = null)
            }
        }

    suspend fun getUserAssignedTasks(userId: String, pageRequest: Pageable): Page<Task> {
        val assignee = Criteria.where("assignedTo").`is`(userId)
        val creator = Criteria.where("createdBy").`is`(userId)
        val query = Query(Criteria().orOperator(assignee, creator)).with(pageRequest)
        return mongoTemplate.find(query, TaskEntity::class.java).asFlow()
            .map { it.toModel() }
            .toList()
            .let { PageImpl(it, pageRequest, mongoTemplate.count(query, TaskEntity::class.java).awaitSingle()) }
    }

    @Transactional
    suspend fun updateTaskTitle(taskId: UUID, title: String): Task =
        updateTask(taskId) { copy(title = title) }

    @Transactional
    suspend fun updateTaskDescription(taskId: UUID, description: String?): Task =
        updateTask(taskId) { copy(description = description) }

    @Transactional
    suspend fun updateTaskPriority(taskId: UUID, priority: TaskPriority): Task =
        updateTask(taskId) { copy(priority = priority) }

    @Transactional
    suspend fun updateTaskAssignee(taskId: UUID, assignee: String?): Task =
        updateTask(taskId) { copy(assignedTo = assignee) }

    @Transactional
    suspend fun updateTaskDueDate(taskId: UUID, dueDate: LocalDate?): Task =
        updateTask(taskId) { copy(dueDate = dueDate) }

    private suspend fun getOrThrow(taskId: UUID): TaskEntity =
        mongoTemplate.findById(taskId, TaskEntity::class.java).awaitSingleOrNull() ?: throw TaskNotFoundException(taskId.toString())

    private suspend fun updateTask(taskId: UUID, update: TaskEntity.() -> TaskEntity): Task {
        return getOrThrow(taskId)
            .update()
            .let { mongoTemplate.save(it).awaitSingle() }
            .toModel()
    }
}