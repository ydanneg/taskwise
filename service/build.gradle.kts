import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    alias(myLibs.plugins.kotlin.jvm)
    alias(myLibs.plugins.springboot)
    alias(myLibs.plugins.kotlin.serialization)
    alias(myLibs.plugins.kotlin.spring)
    alias(myLibs.plugins.springdoc.openapi)
//    id ("org.flywaydb.flyway") version "10.7.1"
//    alias(myLibs.plugins.graalvm)
    application
}

//flyway {
//    url = "jdbc:postgresql://localhost:5432/tasks"
//    user = "dbuser"
//    password = "dbpassword"
//}

application {
    mainClass = "com.ydanneg.taskwise.service.ApplicationKt"
}

dependencies {
    implementation(projects.model)

    val springBom = platform(SpringBootPlugin.BOM_COORDINATES)
    implementation(springBom)
    implementation(myLibs.springboot.starter.actuator)
    implementation(myLibs.springboot.starter.webflux)
    implementation(myLibs.springboot.starter.validation)
    developmentOnly("${myLibs.springboot.devtools.get().module}:${springBom.version}")
    developmentOnly(myLibs.springboot.docker.compose)
    implementation(myLibs.springboot.autoconfigure)
    implementation(myLibs.springdoc.openapi.webflux.ui)
    implementation(myLibs.kotlinx.coroutines.core)
    implementation(myLibs.kotlinx.coroutines.reactor)
    implementation(myLibs.kotlin.reflect)
    implementation(myLibs.spring.data.commons)
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.0") // page/sort encoders
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
//    implementation("org.springframework.data:spring-data-mongodb-reactive")
//    implementation("org.springframework.data:spring-data-mongodb")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
//    implementation("org.flywaydb:flyway-core")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
//    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")

//    runtimeOnly("org.postgresql:postgresql")
//    implementation("org.postgresql:r2dbc-postgresql")

    testImplementation("org.apache.commons:commons-lang3")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
//    testImplementation("org.testcontainers:r2dbc")
//    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:mongodb")

    testImplementation(myLibs.kotest.assertions.core)
    testImplementation(myLibs.springboot.starter.test) {
        exclude(module = "mockito-core")
        exclude(module = "junit")
        exclude(module = "junit-vintage-engine")
        exclude(module = "slf4j-api")
    }
    testImplementation(myLibs.kotlin.test.junit5)
    testImplementation(myLibs.kotlinx.coroutines.core)
}