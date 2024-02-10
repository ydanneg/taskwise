package com.ydanneg.taskwise.service.web.controller

import com.ydanneg.taskwise.BaseTestContainerTest
import com.ydanneg.taskwise.model.CreateTaskRequest
import com.ydanneg.taskwise.model.Task
import com.ydanneg.taskwise.model.TaskPriority.HIGH
import com.ydanneg.taskwise.model.TaskStatus
import com.ydanneg.taskwise.service.web.V1Constants
import com.ydanneg.taskwise.service.web.assertPost
import com.ydanneg.taskwise.test.SpringBootIntegrationTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate
import java.util.UUID
import kotlin.test.Test

@SpringBootIntegrationTest
class UserTaskControllerTest(@Autowired mongoTemplate: ReactiveMongoTemplate) : BaseTestContainerTest(mongoTemplate) {

    @Autowired
    private lateinit var client: WebTestClient

    @Test
    fun `should create task with just a title`() {
        val userId = UUID.randomUUID().toString()
        val request = CreateTaskRequest("Task #1")

        client.assertPost<Task>(V1Constants.userTasksUri(userId), request) {
            expectStatus().isCreated
        }.apply {
            id shouldNotBe null
            title shouldBe request.title
            description shouldBe request.description
            priority shouldBe request.priority
            status shouldBe TaskStatus.TODO
            dueDate shouldBe request.dueDate
            assignee shouldBe request.assignee
            createdBy shouldBe userId
            createdAt shouldNotBe null
            completedAt shouldBe null
        }
    }

    @Test
    fun `should create task with all initial data`() {
        val userId = UUID.randomUUID().toString()
        val request = CreateTaskRequest(
            title = "Task #1",
            description = "Description #1",
            dueDate = LocalDate.now(),
            priority = HIGH,
            assignee = "assignee1"
        )

        client.assertPost<Task>(V1Constants.userTasksUri(userId), request) {
            expectStatus().isCreated
        }.apply {
            id shouldNotBe null
            title shouldBe request.title
            description shouldBe request.description
            priority shouldBe request.priority
            status shouldBe TaskStatus.TODO
            dueDate shouldBe request.dueDate
            assignee shouldBe request.assignee
            createdBy shouldBe userId
            createdAt shouldNotBe null
            completedAt shouldBe null
        }
    }
}