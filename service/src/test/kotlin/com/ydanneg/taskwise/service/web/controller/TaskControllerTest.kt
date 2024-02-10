package com.ydanneg.taskwise.service.web.controller

import com.ydanneg.taskwise.BaseTestContainerTest
import com.ydanneg.taskwise.model.CompletionReport
import com.ydanneg.taskwise.model.CreateTaskRequest
import com.ydanneg.taskwise.model.ErrorDto
import com.ydanneg.taskwise.model.Task
import com.ydanneg.taskwise.model.TaskPriority
import com.ydanneg.taskwise.model.TaskPriority.HIGH
import com.ydanneg.taskwise.model.TaskPriority.LOW
import com.ydanneg.taskwise.model.TaskStatus.DONE
import com.ydanneg.taskwise.model.UpdateTaskAssigneeRequest
import com.ydanneg.taskwise.model.UpdateTaskDescriptionRequest
import com.ydanneg.taskwise.model.UpdateTaskDueDateRequest
import com.ydanneg.taskwise.model.UpdateTaskPriorityRequest
import com.ydanneg.taskwise.model.UpdateTaskStatusRequest
import com.ydanneg.taskwise.model.UpdateTaskTitleRequest
import com.ydanneg.taskwise.service.domain.TaskReportService
import com.ydanneg.taskwise.service.web.V1Constants
import com.ydanneg.taskwise.service.web.assertDelete
import com.ydanneg.taskwise.service.web.assertGet
import com.ydanneg.taskwise.service.web.assertPage
import com.ydanneg.taskwise.service.web.assertPost
import com.ydanneg.taskwise.service.web.assertPut
import com.ydanneg.taskwise.service.web.createAssignedTask
import com.ydanneg.taskwise.service.web.createTask
import com.ydanneg.taskwise.service.web.randomString
import com.ydanneg.taskwise.test.SpringBootIntegrationTest
import io.kotest.matchers.collections.shouldContainAllIgnoringFields
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate
import java.util.UUID
import kotlin.test.Test

@SpringBootIntegrationTest
class TaskControllerTest(@Autowired val mongoTemplate: ReactiveMongoTemplate, @Autowired val reportService: TaskReportService) : BaseTestContainerTest(mongoTemplate) {

    @Autowired
    private lateinit var client: WebTestClient

    @Test
    fun `should get task by id`() {
        val userId = UUID.randomUUID().toString()
        val task = client.assertPost<Task>(V1Constants.userTasksUri(userId), CreateTaskRequest("Task #1")) {
            expectStatus().isCreated
        }

        client.assertGet<Task>(V1Constants.taskByIdUri(task.id)) {
            expectStatus().isOk
        } shouldBe task
    }


    @Test
    fun `should FAIL validating min size of a title and return ErrorDto`() {
        val expected = ErrorDto("BAD_REQUEST", "title: size must be between 1 and 255", mapOf())

        val userId = UUID.randomUUID().toString()
        client.assertPost<ErrorDto>(V1Constants.userTasksUri(userId), CreateTaskRequest("")) {
            expectStatus().isBadRequest
        } shouldBe expected
    }

    @Test
    fun `should return 404 and ErrorDto for unknown task`() {
        val taskId = UUID.randomUUID().toString()
        val expected = ErrorDto("NOT_FOUND", "Task with id $taskId not found", mapOf())

        client.assertGet<ErrorDto>(V1Constants.taskByIdUri(taskId)) {
            expectStatus().isNotFound
        } shouldBe expected
    }

    @Test
    fun `get all tasks page`() {
        val total = 30
        for (i in 1..total) {
            val userId = UUID.randomUUID().toString()
            client.assertPost<Task>(V1Constants.userTasksUri(userId), CreateTaskRequest("Task #$i")) {
                expectStatus().isCreated
            }
        }

        client.assertGet<Page<Task>>(V1Constants.TASKS) {
            expectStatus().isOk
        }.apply {
            assertPage(total)
        }
    }

    @Test
    fun `get all tasks with filter page`() {
        val user = UUID.randomUUID().toString()
        val user2 = UUID.randomUUID().toString()
        for (i in 1..10) {
            client.assertPost<Task>(V1Constants.userTasksUri(user), CreateTaskRequest("Task #$i", priority = LOW, assignee = user)) {
                expectStatus().isCreated
            }
        }
        for (i in 1..10) {
            client.assertPost<Task>(V1Constants.userTasksUri(user), CreateTaskRequest("Task #$i", priority = HIGH, assignee = user)) {
                expectStatus().isCreated
            }
        }
        for (i in 1..10) {
            client.assertPost<Task>(V1Constants.userTasksUri(user), CreateTaskRequest("Task #$i", priority = HIGH, assignee = user2)) {
                expectStatus().isCreated
            }
        }

        client.assertGet<Page<Task>>(V1Constants.TASKS, mapOf("priority" to listOf(HIGH.toString()), "assignee" to listOf(user))) {
            expectStatus().isOk
        }.apply {
            assertPage(10)
        }
        client.assertGet<Page<Task>>(V1Constants.TASKS, mapOf("assignee" to listOf(user))) {
            expectStatus().isOk
        }.apply {
            assertPage(20)
        }
        client.assertGet<Page<Task>>(V1Constants.TASKS, mapOf("assignee" to listOf(user2))) {
            expectStatus().isOk
        }.apply {
            assertPage(10)
        }
        client.assertGet<Page<Task>>(V1Constants.TASKS, mapOf("priority" to listOf(HIGH.toString()))) {
            expectStatus().isOk
        }.apply {
            assertPage(20)
        }
        client.assertGet<Page<Task>>(V1Constants.TASKS, mapOf("priority" to listOf(TaskPriority.MEDIUM.toString()))) {
            expectStatus().isOk
        }.apply {
            assertPage(0)
        }
    }

    @Test
    fun `should update task status`() {
        val task = client.createTask()

        client.assertPut<Task>(V1Constants.taskStatusUri(task.id), UpdateTaskStatusRequest(DONE)) {
            expectStatus().isOk
        }.apply {
            status shouldBe DONE
        }

        client.assertGet<Task>(V1Constants.taskByIdUri(task.id)) {
            expectStatus().isOk
        }.apply {
            status shouldBe DONE
        }
    }

    @Test
    fun `should delete task`() {
        val task = client.createTask()
        client.assertDelete(V1Constants.taskByIdUri(task.id))
        client.assertGet<Any>(V1Constants.taskByIdUri(task.id)) {
            expectStatus().isNotFound
        }
    }

    @Test
    fun `should update task title`() {
        val task = client.createTask()

        val newTitle = randomString(10)
        client.assertPut<Task>(V1Constants.taskTitleUri(task.id), UpdateTaskTitleRequest(newTitle)) {
            expectStatus().isOk
        }.apply {
            title shouldBe newTitle
        }

        client.assertGet<Task>(V1Constants.taskByIdUri(task.id)) {
            expectStatus().isOk
        }.apply {
            title shouldBe newTitle
        }
    }

    @Test
    fun `should update task description`() {
        val task = client.createTask()
        task.description shouldBe null

        val newDescription = randomString(100)
        client.assertPut<Task>(V1Constants.taskDescriptionUri(task.id), UpdateTaskDescriptionRequest(newDescription)) {
            expectStatus().isOk
        }.apply {
            description shouldBe newDescription
        }

        client.assertGet<Task>(V1Constants.taskByIdUri(task.id)) {
            expectStatus().isOk
        }.apply {
            description shouldBe newDescription
        }
    }

    @Test
    fun `should update task priority`() {
        val task = client.createTask()
        task.priority shouldBe LOW

        client.assertPut<Task>(V1Constants.taskPriorityUri(task.id), UpdateTaskPriorityRequest(HIGH)) {
            expectStatus().isOk
        }.apply {
            priority shouldBe HIGH
        }

        client.assertGet<Task>(V1Constants.taskByIdUri(task.id)) {
            expectStatus().isOk
        }.apply {
            priority shouldBe HIGH
        }
    }

    @Test
    fun `should update task assignee`() {
        val task = client.createTask()
        task.assignee shouldBe null

        val assignee = UUID.randomUUID().toString()
        client.assertPut<Task>(V1Constants.taskAssigneeUri(task.id), UpdateTaskAssigneeRequest(assignee)) {
            expectStatus().isOk
        }.apply {
            this.assignee shouldBe assignee
        }

        client.assertGet<Task>(V1Constants.taskByIdUri(task.id)) {
            expectStatus().isOk
        }.apply {
            this.assignee shouldBe assignee
        }
    }

    @Test
    fun `should update task due date`() {
        val task = client.createTask()
        task.dueDate shouldBe null

        val dueDate = LocalDate.now().plusDays(1)
        client.assertPut<Task>(V1Constants.taskDueDateUri(task.id), UpdateTaskDueDateRequest(dueDate)) {
            expectStatus().isOk
        }.apply {
            dueDate shouldBe dueDate
        }

        client.assertGet<Task>(V1Constants.taskByIdUri(task.id)) {
            expectStatus().isOk
        }.apply {
            dueDate shouldBe dueDate
        }
    }

    @Test
    fun `should get report`() = runTest {
        val assignee = UUID.randomUUID().toString()
        for (i in 1..10) {
            client.createAssignedTask(assignee, "Task #$i for $assignee", DONE)
        }
        val assignee2 = UUID.randomUUID().toString()
        for (i in 1..5) {
            client.createAssignedTask(assignee2, "Task #$i for $assignee2", DONE)
        }

        val expected = listOf(
            CompletionReport(assignee = assignee, total = 10, avgTime = 0),
            CompletionReport(assignee = assignee2, total = 5, avgTime = 0)
        )
        reportService.getCompletionSummary().apply {
            size shouldBe 2
            this.shouldContainAllIgnoringFields(expected, CompletionReport::avgTime)
        }
    }
}