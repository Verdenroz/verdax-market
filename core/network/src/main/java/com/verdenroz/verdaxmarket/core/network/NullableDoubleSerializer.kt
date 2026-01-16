package com.verdenroz.verdaxmarket.core.network

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull

/**
 * Custom serializer for nullable Double values that handles empty JSON objects.
 * The API returns {} for unavailable numeric values instead of null.
 */
object NullableDoubleSerializer : KSerializer<Double?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("NullableDouble", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Double? {
        val jsonDecoder = decoder as? JsonDecoder ?: return decoder.decodeDouble()

        return when (val element: JsonElement = jsonDecoder.decodeJsonElement()) {
            is JsonNull -> null
            is JsonObject -> null // Treat empty objects {} as null
            is JsonPrimitive -> element.doubleOrNull
            else -> null
        }
    }

    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Double?) {
        if (value != null) {
            encoder.encodeDouble(value)
        } else {
            encoder.encodeNull()
        }
    }
}

/**
 * Custom serializer for nullable Long values that handles empty JSON objects.
 * The API returns {} for unavailable numeric values instead of null.
 */
object NullableLongSerializer : KSerializer<Long?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("NullableLong", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): Long? {
        val jsonDecoder = decoder as? JsonDecoder ?: return decoder.decodeLong()

        return when (val element: JsonElement = jsonDecoder.decodeJsonElement()) {
            is JsonNull -> null
            is JsonObject -> null // Treat empty objects {} as null
            is JsonPrimitive -> element.content.toLongOrNull()
            else -> null
        }
    }

    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Long?) {
        if (value != null) {
            encoder.encodeLong(value)
        } else {
            encoder.encodeNull()
        }
    }
}
