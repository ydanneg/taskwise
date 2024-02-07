package com.ydanneg.taskwise

import com.ydanneg.taskwise.service.data.TaskRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
abstract class BaseTestContainerTest(private val taskRepository: TaskRepository) {

    @BeforeEach
    fun setup() = runBlocking {
        taskRepository.deleteAll()
    }

    companion object {

        @ServiceConnection
        private val postgres = PostgreSQLContainer(DockerImageName.parse("postgres:15-alpine"))

        init {
            postgres.start()
        }
    }

}