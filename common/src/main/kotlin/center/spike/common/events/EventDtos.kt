package center.spike.common.events

import kotlinx.serialization.Serializable

@Serializable
data class GeneratePitScheduleRequest(
    val eventCode: String,
    val scouters: List<String>
)

@Serializable
data class GenerateMatchScheduleRequest(
    val eventCode: String,
    val groupAScouters: Map<AssignmentType, String>,
    val groupBScouters: Map<AssignmentType, String>,
    val groupALeader: String,
    val groupBLeader: String
)