package com.ydanneg.taskwise.service.web.controller

import com.ydanneg.taskwise.BaseTestContainerTest
import com.ydanneg.taskwise.model.CreateTaskRequest
import com.ydanneg.taskwise.model.Task
import com.ydanneg.taskwise.model.TaskPriority.HIGH
import com.ydanneg.taskwise.model.TaskStatus
import com.ydanneg.taskwise.service.data.TaskRepository
import com.ydanneg.taskwise.service.web.V1Constants
import com.ydanneg.taskwise.service.web.assertGet
import com.ydanneg.taskwise.service.web.assertPage
import com.ydanneg.taskwise.service.web.assertPost
import com.ydanneg.taskwise.test.SpringBootIntegrationTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate
import java.util.UUID
import kotlin.test.Test

@SpringBootIntegrationTest
class UserTaskControllerTest(@Autowired taskRepository: TaskRepository) : BaseTestContainerTest(taskRepository) {

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
            assignedTo shouldBe request.assignedTo
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
            assignedTo = "assignee1"
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
            assignedTo shouldBe request.assignedTo
            createdBy shouldBe userId
            createdAt shouldNotBe null
            completedAt shouldBe null
        }
    }

    @Test
    fun `should return all user tasks by page`() {
        val userId = UUID.randomUUID().toString()
        val total = 10
        for (i in 1..total) {
            client.assertPost<Task>(V1Constants.userTasksUri(userId), CreateTaskRequest("Task #$i", assignedTo = userId)) {
                expectStatus().isCreated
            }
        }

        client.assertGet<Page<Task>>(V1Constants.userTasksUri(userId)) {
            expectStatus().isOk
        }.apply {
            assertPage(total)
        }

    }

}