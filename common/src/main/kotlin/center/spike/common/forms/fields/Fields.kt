package center.spike.common.forms.fields

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("kind")
abstract class FieldAnswer {
    abstract val fieldId: String
    abstract val type: FieldType
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("kind")
abstract class FieldDefinition {
    abstract val id: String
    abstract val label: String
    abstract val type: FieldType
    abstract val required: Boolean
    abstract val description: String?
}

@Serializable

enum class FieldType {
    @SerialName("avg_stopwatch") AVG_STOPWATCH,
    @SerialName("category") CATEGORY,
    @SerialName("checkmark") CHECKMARK,
    @SerialName("dropdown_multi") DROPDOWN_MULTI,
    @SerialName("dropdown_single") DROPDOWN_SINGLE,
    @SerialName("matrix_number") MATRIX_NUMBER,
    @SerialName("number") NUMBER,
    @SerialName("paragraph") PARAGRAPH,
    @SerialName("picture") PICTURE,
    @SerialName("radio") RADIO,
    @SerialName("range") RANGE,
    @SerialName("short_text") SHORT_TEXT,
    @SerialName("sketch") SKETCH,
    @SerialName("stopwatch") STOPWATCH
}