package com.ydanneg.taskwise

import com.ydanneg.taskwise.service.data.TaskEntity
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
abstract class BaseTestContainerTest(private val mongoDbTemplate: ReactiveMongoTemplate) {

    @BeforeEach
    fun setup()  {
        mongoDbTemplate.dropCollection(TaskEntity::class.java).block()
    }

    companion object {

        @ServiceConnection
        private val mongoDb = MongoDBContainer(DockerImageName.parse("mongo:5.0.3"))

        init {
            mongoDb.start()
        }
    }

}