package com.ydanneg.taskwise.service.domain

import com.ydanneg.taskwise.model.CompletionReport
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.sort
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant


@Service
@Transactional(readOnly = true)
class TaskReportService(val mongoTemplate: ReactiveMongoTemplate) {

    /**
     * Get the average time to complete a task and the total number of tasks completed by each assignee
     */
    suspend fun getCompletionSummary(startDate: Instant? = null, endDate: Instant? = null): List<CompletionReport> {
        val aggregation = newAggregation(
            match(
                Criteria.where("status").`is`("DONE").apply {
                    if (startDate != null) and("completedAt").gte(startDate)
                    if (endDate != null) and("completedAt").lte(endDate)
                }
            ),
            project("assignee")
                .andExpression("completedAt - createdAt").`as`("duration"),
            group("assignee")
                .avg("duration").`as`("avgTime")
                .count().`as`("total"),
            project("avgTime", "total")
                .andExpression("_id").`as`("assignee"),
            sort(DESC, "total")
        )
        return mongoTemplate.aggregate(aggregation, "tasks", CompletionReport::class.java)
            .asFlow()
            .toList()
    }
}