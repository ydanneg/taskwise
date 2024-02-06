package com.ydanneg.taskwise.service.data

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
interface TaskRepository : CoroutineCrudRepository<TaskEntity, UUID> {

    fun findByCreatedBy(userId: String): Flow<TaskEntity>

    fun findByAssignedTo(userId: String): Flow<TaskEntity>

    suspend fun countByAssignedTo(userId: String): Long

}