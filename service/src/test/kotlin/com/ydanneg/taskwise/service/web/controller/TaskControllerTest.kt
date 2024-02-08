package com.ydanneg.taskwise.service.web.controller

import com.ydanneg.taskwise.BaseTestContainerTest
import com.ydanneg.taskwise.model.CreateTaskRequest
import com.ydanneg.taskwise.model.ErrorDto
import com.ydanneg.taskwise.model.Task
import com.ydanneg.taskwise.model.TaskPriority.HIGH
import com.ydanneg.taskwise.model.TaskPriority.LOW
import com.ydanneg.taskwise.model.TaskStatus
import com.ydanneg.taskwise.model.UpdateTaskAssigneeRequest
import com.ydanneg.taskwise.model.UpdateTaskDescriptionRequest
import com.ydanneg.taskwise.model.UpdateTaskDueDateRequest
import com.ydanneg.taskwise.model.UpdateTaskPriorityRequest
import com.ydanneg.taskwise.model.UpdateTaskStatusRequest
import com.ydanneg.taskwise.model.UpdateTaskTitleRequest
import com.ydanneg.taskwise.service.web.V1Constants
import com.ydanneg.taskwise.service.web.assertDelete
import com.ydanneg.taskwise.service.web.assertGet
import com.ydanneg.taskwise.service.web.assertPage
import com.ydanneg.taskwise.service.web.assertPost
import com.ydanneg.taskwise.service.web.assertPut
import com.ydanneg.taskwise.service.web.randomString
import com.ydanneg.taskwise.test.SpringBootIntegrationTest
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate
import java.util.UUID
import kotlin.test.Test

@SpringBootIntegrationTest
class TaskControllerTest(@Autowired mongoTemplate: ReactiveMongoTemplate) : BaseTestContainerTest(mongoTemplate) {

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
    fun `should update task status`() {
        val task = createTask()

        client.assertPut<Task>(V1Constants.taskStatusUri(task.id), UpdateTaskStatusRequest(TaskStatus.COMPLETED)) {
            expectStatus().isOk
        }.apply {
            status shouldBe TaskStatus.COMPLETED
        }

        client.assertGet<Task>(V1Constants.taskByIdUri(task.id)) {
            expectStatus().isOk
        }.apply {
            status shouldBe TaskStatus.COMPLETED
        }
    }

    @Test
    fun `should delete task`() {
        val task = createTask()
        client.assertDelete(V1Constants.taskByIdUri(task.id))
        client.assertGet<Any>(V1Constants.taskByIdUri(task.id)) {
            expectStatus().isNotFound
        }
    }

    @Test
    fun `should update task title`() {
        val task = createTask()

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
        val task = createTask()
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
        val task = createTask()
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
        val task = createTask()
        task.assignedTo shouldBe null

        val assignee = UUID.randomUUID().toString()
        client.assertPut<Task>(V1Constants.taskAssigneeUri(task.id), UpdateTaskAssigneeRequest(assignee)) {
            expectStatus().isOk
        }.apply {
            assignedTo shouldBe assignee
        }

        client.assertGet<Task>(V1Constants.taskByIdUri(task.id)) {
            expectStatus().isOk
        }.apply {
            assignedTo shouldBe assignee
        }
    }

    @Test
    fun `should update task due date`() {
        val task = createTask()
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

    private fun createTask(userId: String = UUID.randomUUID().toString(), title: String = randomString(10)): Task =
        client.assertPost<Task>(V1Constants.userTasksUri(userId), CreateTaskRequest(title)) {
            expectStatus().isCreated
        }

}