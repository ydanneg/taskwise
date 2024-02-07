package com.ydanneg.taskwise.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.ResolverStyle


@Serializer(forClass = Instant::class)
object KInstantSerializer : KSerializer<Instant> {
    private val formatter = DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendInstant(3)
        .toFormatter()

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(formatter.format(value))
    }

    override fun deserialize(decoder: Decoder): Instant = Instant.parse(decoder.decodeString())
}

@Serializer(forClass = LocalDate::class)
object KLocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(DateTimeFormatter.ISO_LOCAL_DATE.format(value))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString())
    }
}
