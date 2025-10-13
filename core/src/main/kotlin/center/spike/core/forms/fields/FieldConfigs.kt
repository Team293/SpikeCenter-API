package center.spike.core.forms.fields

import center.spike.common.forms.fields.FieldDefinition
import center.spike.common.forms.fields.FieldType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("avg_stopwatch")
class AverageStopwatchField(
    override val id: String,
    override val label: String,
    override val type: FieldType = FieldType.AVG_STOPWATCH,
    override val required: Boolean = false,
    override val description: String? = null,
) : FieldDefinition()

@Serializable
@SerialName("category")
class CategoryField(
    override val id: String,
    override val label: String,
    override val type: FieldType = FieldType.CATEGORY,
    override val required: Boolean = false,
    override val description: String? = null,
    val children: List<FieldDefinition>
) : FieldDefinition()

@Serializable
@SerialName("checkmark")
class CheckmarkField(
    override val id: String,
    override val label: String,
    override val type: FieldType = FieldType.CHECKMARK,
    override val required: Boolean = false,
    override val description: String? = null,
    val default : Boolean = false
) : FieldDefinition()

@Serializable
@SerialName("dropdown_multi")
class DropdownMultiField(
    override val id: String,
    override val label: String,
    override val type: FieldType = FieldType.DROPDOWN_MULTI,
    override val required: Boolean = false,
    override val description: String? = null,
    val options: List<String>,
    val maxSelections: Int? = null,
) : FieldDefinition()

@Serializable
@SerialName("dropdown_single")
class DropdownSingleField(
    override val id: String,
    override val label: String,
    override val type: FieldType = FieldType.DROPDOWN_SINGLE,
    override val required: Boolean = false,
    override val description: String? = null,
    val options: List<String>,
) : FieldDefinition()

@Serializable
@SerialName("matrix_number")
class MatrixNumberField(
    override val id: String,
    override val label: String,
    override val type: FieldType = FieldType.MATRIX_NUMBER,
    override val required: Boolean = false,
    override val description: String? = null,
    val rows: List<MatrixNumberRow>,
) : FieldDefinition()

// Matrix row
@Serializable
class MatrixNumberRow(
    val id: String,
    val label: String,
    val min: Int? = null,
    val max: Int? = null,
    val default: Int? = null,
)

@Serializable
@SerialName("number")
class NumberField(
    override val id: String,
    override val label: String,
    override val type: FieldType = FieldType.NUMBER,
    override val required: Boolean = false,
    override val description: String? = null,
    val min: Double? = null,
    val max: Double? = null,
    val startingNumber: Double? = null,
) : FieldDefinition()

@Serializable
@SerialName("paragraph")
class ParagraphField(
    override val id: String,
    override val label: String,
    override val type: FieldType = FieldType.PARAGRAPH,
    override val required: Boolean = false,
    override val description: String? = null,
    val characterLimit: Int? = null,
    val placeholderText: String? = null,
) : FieldDefinition()

@Serializable
@SerialName("picture")
class PictureField(
    override val id: String,
    override val label: String,
    override val type: FieldType = FieldType.PICTURE,
    override val required: Boolean = false,
    override val description: String? = null,
    val min: Int? = null,
    val max: Int? = null,
) : FieldDefinition()

@Serializable
@SerialName("range")
class RangeField(
    override val id: String,
    override val label: String,
    override val type: FieldType = FieldType.RANGE,
    override val required: Boolean = false,
    override val description: String? = null,
    val min: Int,
    val max: Int,
    val default: Int? = null,
) : FieldDefinition()

@Serializable
@SerialName("radio")
class RadioField(
    override val id: String,
    override val label: String,
    override val type: FieldType = FieldType.RADIO,
    override val required: Boolean = false,
    override val description: String? = null,
    val options: List<String>,
) : FieldDefinition()

@Serializable
@SerialName("short_text")
class ShortTextField(
    override val id: String,
    override val label: String,
    override val type: FieldType = FieldType.SHORT_TEXT,
    override val required: Boolean = false,
    override val description: String? = null,
    val characterLimit: Int? = null,
    val placeholderText: String? = null,
) : FieldDefinition()

@Serializable
@SerialName("sketch")
class SketchField(
    override val id: String,
    override val label: String,
    override val type: FieldType = FieldType.SKETCH,
    override val required: Boolean = false,
    override val description: String? = null,
    val backgroundImageUrl: String? = null,
) : FieldDefinition()

@Serializable
@SerialName("stopwatch")
class StopwatchField(
    override val id: String,
    override val label: String,
    override val type: FieldType = FieldType.STOPWATCH,
    override val required: Boolean = false,
    override val description: String? = null,
) : FieldDefinition()

