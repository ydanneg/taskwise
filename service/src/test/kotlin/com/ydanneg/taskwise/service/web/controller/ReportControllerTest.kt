package com.ydanneg.taskwise.service.web.controller

import com.ydanneg.taskwise.BaseTestContainerTest
import com.ydanneg.taskwise.model.CompletionReport
import com.ydanneg.taskwise.model.TaskStatus.DONE
import com.ydanneg.taskwise.model.TaskStatus.IN_PROGRESS
import com.ydanneg.taskwise.model.TaskStatus.TODO
import com.ydanneg.taskwise.service.domain.TaskReportService
import com.ydanneg.taskwise.service.web.createAssignedTask
import com.ydanneg.taskwise.service.web.createTask
import com.ydanneg.taskwise.test.SpringBootIntegrationTest
import io.kotest.matchers.collections.shouldContainAllIgnoringFields
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID
import kotlin.test.Test

@SpringBootIntegrationTest
class ReportControllerTest(
    @Autowired val reportService: TaskReportService,
    @Autowired mongoTemplate: ReactiveMongoTemplate
) : BaseTestContainerTest(mongoTemplate) {

    @Autowired
    private lateinit var client: WebTestClient

    @Test
    fun `should get completion report`() = runTest {
        val assignee = UUID.randomUUID().toString()
        client.createAssignedTask(assignee, "Task #0 for $assignee", TODO)
        for (i in 1..10) {
            client.createAssignedTask(assignee, "Task #$i for $assignee", DONE)
        }
        val assignee2 = UUID.randomUUID().toString()
        client.createAssignedTask(assignee, "Task #0 for $assignee2", IN_PROGRESS)
        for (i in 1..5) {
            client.createAssignedTask(assignee2, "Task #$i for $assignee2", DONE)
        }
        client.createTask("new task")
        

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