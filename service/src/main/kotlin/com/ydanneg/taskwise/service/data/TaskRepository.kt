package com.ydanneg.taskwise.service.data

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
interface TaskRepository : CoroutineCrudRepository<TaskEntity, UUID> {

    fun findByCreatedBy(userId: String): Flow<TaskEntity>

    fun findByAssignedTo(userId: String, pageRequest: Pageable): Flow<TaskEntity>

    suspend fun countByAssignedTo(userId: String): Long

    fun findBy(pageRequest: Pageable): Flow<TaskEntity>

}