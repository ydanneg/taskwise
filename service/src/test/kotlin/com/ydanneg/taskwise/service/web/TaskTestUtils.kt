package com.ydanneg.taskwise.service.web

import io.kotest.matchers.shouldBe
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import kotlin.math.ceil

inline fun <reified T> WebTestClient.assertPost(uri: String, request: Any, block: ResponseSpec.() -> Unit): T {
    return post()
        .uri(uri)
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(request))
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .also(block)
        .expectBody(T::class.java)
        .returnResult().responseBody!!
}

inline fun <reified T> WebTestClient.assertPut(uri: String, request: Any, block: ResponseSpec.() -> Unit): T {
    return put()
        .uri(uri)
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(request))
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .also(block)
        .expectBody(T::class.java)
        .returnResult().responseBody!!
}

fun WebTestClient.assertDelete(uri: String) {
    delete()
        .uri(uri)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isNoContent
}

inline fun <reified T> WebTestClient.assertGet(uri: String, params: Map<String, List<String>> = mapOf(), block: ResponseSpec.() -> Unit): T {
    return get()
        .uri { it.path(uri).queryParams(LinkedMultiValueMap(params)).build() }
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .also(block)
        .expectBody(T::class.java)
        .returnResult().responseBody!!
}

fun randomString(length: Int) = RandomStringUtils.randomAlphanumeric(length)

fun Page<*>.assertPage(total: Int) {
    totalPages shouldBe ceil(total.toDouble() / V1Constants.DEFAULT_PAGE_SIZE)
    totalElements shouldBe total
    size shouldBe V1Constants.DEFAULT_PAGE_SIZE
    content.size shouldBe total.coerceAtMost(V1Constants.DEFAULT_PAGE_SIZE)
}