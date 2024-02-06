package com.ydanneg.taskwise.service.web

import com.ydanneg.taskwise.model.CreateTaskRequest
import com.ydanneg.taskwise.model.Task
import com.ydanneg.taskwise.model.TaskPriority
import com.ydanneg.taskwise.model.TaskStatus
import com.ydanneg.taskwise.test.SpringBootIntegrationTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import kotlin.test.Test


@SpringBootIntegrationTest
@Testcontainers
class TaskControllerTest {

    companion object {

        @Container
        @ServiceConnection
        private val postgres = PostgreSQLContainer(DockerImageName.parse("postgres:15-alpine"))
    }

    @Autowired
    private lateinit var client: WebTestClient

    @Test
    fun test() {
        val request = CreateTaskRequest("Hello World", "This is a description")

        val body = client.post()
            .uri(V1Constants.USER_TASKS.replace("{userId}", "user1"))
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isCreated
            .expectBody(Task::class.java)
            .returnResult().responseBody

        body shouldNotBe null
        with(body!!) {
            title shouldBe request.title
            description shouldBe request.description
            id shouldNotBe null
            priority shouldBe TaskPriority.LOW
            status shouldBe TaskStatus.TODO
            dueDate shouldBe null
            assignedTo shouldBe null
            createdBy shouldBe "user1"
            createdAt shouldNotBe null
            completedAt shouldBe null
        }
    }

}