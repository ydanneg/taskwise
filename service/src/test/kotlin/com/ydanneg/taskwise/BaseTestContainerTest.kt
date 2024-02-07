package com.ydanneg.taskwise

import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
abstract class BaseTestContainerTest {

    companion object {

        @ServiceConnection
        private val postgres = PostgreSQLContainer(DockerImageName.parse("postgres:15-alpine"))

        init {
            postgres.start()
        }
    }

}