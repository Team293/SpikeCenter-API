package center.spike.common.dashboard

import center.spike.common.forms.FormType
import kotlinx.serialization.Serializable

@Serializable
data class CreateDashboardChipRequest(
    val code: String,
    val filters: MutableList<FilterType>,
    val renderType: RenderType,
    val formInputType: FormType,
    val title: String,
    val variableName: String? = null
)

@Serializable
data class UpdateDashboardChipRequest(
    val id: Long,
    val code: String?,
    val filters: MutableList<FilterType>?,
    val renderType: RenderType?,
    val formInputType: FormType?,
    val title: String?,
    val variableName: String? = null
)