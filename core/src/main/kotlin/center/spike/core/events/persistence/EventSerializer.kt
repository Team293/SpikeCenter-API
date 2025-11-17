package center.spike.core.events.persistence

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object EventCodeSerializer : KSerializer<Event> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("EventCode", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Event) {
        encoder.encodeString(value.code)
    }

    override fun deserialize(decoder: Decoder): Event {
        throw UnsupportedOperationException("Deserialization is not supported for EventCodeSerializer")
    }
}
