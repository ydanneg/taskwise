package com.ydanneg.taskwise.service.domain

import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TaskReportService(val mongoTemplate: ReactiveMongoTemplate) {

    fun getReportByAssignee(assignee: String) {

    }
}