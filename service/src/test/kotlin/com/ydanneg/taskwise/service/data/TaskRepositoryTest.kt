package com.ydanneg.taskwise.service.data

import com.ydanneg.taskwise.BaseTestContainerTest
import com.ydanneg.taskwise.model.TaskStatus
import com.ydanneg.taskwise.test.SpringBootIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Example

@SpringBootIntegrationTest
class TaskRepositoryTest(@Autowired taskRepository: TaskRepository) : BaseTestContainerTest(taskRepository) {
    
}