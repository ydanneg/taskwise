package com.ydanneg.taskwise.service.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.cloud.openfeign.support.PageJacksonModule
import org.springframework.cloud.openfeign.support.SortJacksonModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder
import org.springframework.http.codec.json.KotlinSerializationJsonEncoder
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer


@Configuration
@EnableWebFlux
@EnableR2dbcAuditing
class ApplicationConfig : WebFluxConfigurer {

    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(ReactivePageableHandlerMethodArgumentResolver())
    }

    @Bean
    fun kotlinSerializationJsonEncoder(): KotlinSerializationJsonEncoder =
        KotlinSerializationJsonEncoder(Serializers.json)

    @Bean
    fun kotlinSerializationJsonDecoder(): KotlinSerializationJsonDecoder =
        KotlinSerializationJsonDecoder(Serializers.json)

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        configurer.defaultCodecs().kotlinSerializationJsonEncoder(kotlinSerializationJsonEncoder())
        configurer.defaultCodecs().kotlinSerializationJsonDecoder(kotlinSerializationJsonDecoder())
        configurer.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper(), MediaType.APPLICATION_JSON))
        configurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper(), MediaType.APPLICATION_JSON))
    }

    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule(Jdk8Module())
        .registerModule(JavaTimeModule())
        .registerModule(PageJacksonModule())
        .registerModule(SortJacksonModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)

}
