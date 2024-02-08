package com.ydanneg.taskwise.service.domain

import com.ydanneg.taskwise.model.TaskPriority
import com.ydanneg.taskwise.model.TaskStatus
import com.ydanneg.taskwise.service.data.TaskEntity
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

class EntityToModelMapperTest {

    @Test
    fun `should map entity to model`() {
        // given
        val entity = TaskEntity(
            id = UUID.randomUUID(),
            title = "title",
            description = "description",
            status = TaskStatus.COMPLETED,
            priority = TaskPriority.HIGH,
            dueDate = LocalDate.now(),
            assignedTo = "assignedTo",
            createdAt = Instant.now(),
            createdBy = "createdBy",
            completedAt = Instant.now()
        )

        // when
        val model = entity.toModel()

        // then
        model.id shouldBe entity.id.toString()
        model.title shouldBe entity.title
        model.description shouldBe entity.description
        model.status shouldBe entity.status
        model.priority shouldBe entity.priority
        model.dueDate shouldBe entity.dueDate
        model.assignedTo shouldBe entity.assignedTo
        model.createdAt shouldBe entity.createdAt!!
        model.createdBy shouldBe entity.createdBy
        model.completedAt shouldBe entity.completedAt
    }
}