package center.spike.core.forms.fields

import center.spike.common.forms.fields.FieldAnswer
import center.spike.common.forms.fields.FieldType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("avg_stopwatch")
class AverageStopwatchAnswer(
    override val fieldId: String,
    val times: List<Long> // in milliseconds
) : FieldAnswer() {
    override val type: FieldType = FieldType.AVG_STOPWATCH
}

@Serializable
@SerialName("checkmark")
class CheckmarkAnswer(
    override val fieldId: String,
    val value: Boolean
) : FieldAnswer() {
    override val type: FieldType = FieldType.CHECKMARK
}

@Serializable
@SerialName("dropdown_multi")
class DropdownMultiAnswer(
    override val fieldId: String,
    val value: List<String>
) : FieldAnswer() {
    override val type: FieldType = FieldType.DROPDOWN_MULTI
}

@Serializable
@SerialName("dropdown_single")
class DropdownSingleAnswer(
    override val fieldId: String,
    val value: String
) : FieldAnswer() {
    override val type: FieldType = FieldType.DROPDOWN_SINGLE
}

@Serializable
@SerialName("matrix_number")
class MatrixNumberAnswer(
    override val fieldId: String,
    val value: Map<String, Double> // rowId -> number
) : FieldAnswer() {
    override val type: FieldType = FieldType.MATRIX_NUMBER
}

@Serializable
@SerialName("number")
class NumberAnswer(
    override val fieldId: String,
    val value: Double
) : FieldAnswer() {
    override val type: FieldType = FieldType.NUMBER
}

@Serializable
@SerialName("paragraph")
class ParagraphAnswer(
    override val fieldId: String,
    val value: String
) : FieldAnswer() {
    override val type: FieldType = FieldType.PARAGRAPH
}

@Serializable
@SerialName("picture")
class PictureAnswer(
    override val fieldId: String,
    val value: List<String> // base64
) : FieldAnswer() {
    override val type: FieldType = FieldType.PICTURE
}

@Serializable
@SerialName("radio")
class RadioAnswer(
    override val fieldId: String,
    val value: String
) : FieldAnswer() {
    override val type: FieldType = FieldType.RADIO
}

@Serializable
@SerialName("range")
class RangeAnswer(
    override val fieldId: String,
    val value: Double
) : FieldAnswer() {
    override val type: FieldType = FieldType.RANGE
}

@Serializable
@SerialName("short_text")
class ShortTextAnswer(
    override val fieldId: String,
    val value: String
) : FieldAnswer() {
    override val type: FieldType = FieldType.SHORT_TEXT
}

@Serializable
@SerialName("sketch")
class SketchAnswer(
    override val fieldId: String,
    val value: String // base64
) : FieldAnswer() {
    override val type: FieldType = FieldType.SKETCH
}

@Serializable
@SerialName("stopwatch")
class StopwatchAnswer(
    override val fieldId: String,
    val time: Long // in milliseconds
) : FieldAnswer() {
    override val type: FieldType = FieldType.STOPWATCH
}
