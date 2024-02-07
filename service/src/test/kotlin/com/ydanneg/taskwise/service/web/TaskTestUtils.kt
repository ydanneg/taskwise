package com.ydanneg.taskwise.service.web

import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec
import org.springframework.web.reactive.function.BodyInserters

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

inline fun <reified T> WebTestClient.assertGet(uri: String, block: ResponseSpec.() -> Unit): T {
    return get()
        .uri(uri)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .also(block)
        .expectBody(T::class.java)
        .returnResult().responseBody!!
}
