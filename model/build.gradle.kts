plugins {
    alias(myLibs.plugins.kotlin.jvm)
    alias(myLibs.plugins.kotlin.serialization)
    `java-library`
}

dependencies {
    api("jakarta.validation:jakarta.validation-api:3.0.2")
    api(myLibs.kotlinx.serialization.json)
}