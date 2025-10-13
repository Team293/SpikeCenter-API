package center.spike.common.forms

import center.spike.common.forms.fields.FieldAnswer
import center.spike.common.forms.fields.FieldDefinition
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class CreateFormRequest(
    val type: FormType
)

@Serializable
data class CreateVersionRequest(
    val formId: Long,
    val schema: JsonElement
)

@Serializable
data class FormIdTransit(
    val formId: Long
)

@Serializable
data class GetVersionRequest(
    val formId: Long,
    val version: Int
)

@Serializable
data class SubmitFormRequest(
    val formId: Long,
    val teamNumber: Long,
    val fieldResponses: List<FieldAnswer>
)

@Serializable
data class GetFormByTypeRequest(
    val type: FormType
)


@Serializable
data class FormVersionResponse(
    val id: Long,
    val formId: Long,
    val version: Int,
    val schema: List<FieldDefinition>
)

@Serializable
data class FormResponsesResponse(
    val id: Long,
    val formId: Long,
    val teamNumber: Long,
    val responseType: FormType,
    val eventCode: String?,
    val matchCode: String?,
    val fieldResponses: List<FieldAnswer>
)