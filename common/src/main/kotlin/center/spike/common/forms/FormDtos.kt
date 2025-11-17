package center.spike.common.forms

import center.spike.common.forms.fields.FieldAnswer
import center.spike.common.forms.fields.FieldDefinition
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class CreateFormRequest(
    val type: ScoutingType
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
    val matchCode: String?,
    val fieldResponses: List<FieldAnswer>
)

@Serializable
data class GetFormByTypeRequest(
    val type: ScoutingType
)

@Serializable
data class SetVersionRequest(
    val formId: Long,
    val version: Int
)

@Serializable
data class GetFormResponsesWithEventCodeRequest(
    val formId: Long,
    val eventCode: String
)

@Serializable
data class GetFormResponsesWithTeamNumberRequest(
    val formId: Long,
    val teamNumber: Long
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
    val responseType: ScoutingType,
    val eventCode: String?,
    val matchCode: String?,
    val fieldResponses: List<FieldAnswer>
)