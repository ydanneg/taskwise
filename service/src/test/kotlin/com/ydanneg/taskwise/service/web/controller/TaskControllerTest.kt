package com.ydanneg.taskwise.service.web.controller

import com.ydanneg.taskwise.BaseTestContainerTest
import com.ydanneg.taskwise.model.CreateTaskRequest
import com.ydanneg.taskwise.model.ErrorDto
import com.ydanneg.taskwise.model.Task
import com.ydanneg.taskwise.service.web.V1Constants
import com.ydanneg.taskwise.service.web.assertGet
import com.ydanneg.taskwise.service.web.assertPost
import com.ydanneg.taskwise.test.SpringBootIntegrationTest
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID
import kotlin.test.Test

@SpringBootIntegrationTest
class TaskControllerTest : BaseTestContainerTest() {

    @Autowired
    private lateinit var client: WebTestClient

    @Test
    fun `should get task by id`() {
        val task = client.assertPost<Task>(V1Constants.userTasksUri("user1"), CreateTaskRequest("Task #1")) {
            expectStatus().isCreated
        }

        client.assertGet<Task>(V1Constants.taskByIdUri(task.id)) {
            expectStatus().isOk
        } shouldBe task
    }


    @Test
    fun `should FAIL validating min size of a title and return ErrorDto`() {
        val expected = ErrorDto("BAD_REQUEST", "title: size must be between 1 and 255", mapOf())

        client.assertPost<ErrorDto>(V1Constants.userTasksUri("user1"), CreateTaskRequest("")) {
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

}