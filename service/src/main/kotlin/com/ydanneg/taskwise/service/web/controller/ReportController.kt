package com.ydanneg.taskwise.service.web.controller

import com.ydanneg.taskwise.model.CompletionReportResponse
import com.ydanneg.taskwise.service.domain.TaskReportService
import com.ydanneg.taskwise.service.web.V1Constants
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping(V1Constants.REPORTS)
@Tag(name = "Reports", description = "Endpoints for task reports")
class ReportController(private val taskReportService: TaskReportService) {

    @GetMapping("/completed")
    @ResponseStatus(OK)
    @Operation(summary = "Get the completion report")
    suspend fun getTask(
        @RequestParam(required = false) startDate: Instant? = null,
        @RequestParam(required = false) endDate: Instant? = null
    ) = CompletionReportResponse(taskReportService.getCompletionSummary(startDate, endDate))
}