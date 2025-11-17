package center.spike.core.schedule.services

import center.spike.common.ErrorCode
import center.spike.common.ResponseStatus
import center.spike.common.ServiceError
import center.spike.common.ServiceResponse
import center.spike.common.events.AssignmentType
import center.spike.core.events.persistence.EventRepository
import center.spike.core.schedule.persistence.Assignment
import center.spike.core.schedule.persistence.AssignmentRepository
import center.spike.core.users.services.KeycloakRoles
import center.spike.core.users.services.UserDirectoryService
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional

@ApplicationScoped
class ScheduleService {

    @Inject
    internal lateinit var userDirectoryService: UserDirectoryService

    @Inject
    internal lateinit var assignmentRepository: AssignmentRepository

    @Inject
    internal lateinit var eventRepository: EventRepository

    @Transactional
    fun createPitSchedule(eventCode: String, scouters: List<String>): ServiceResponse<Unit> {
        val event = eventRepository.findById(eventCode)
            ?: return ServiceResponse(
                status = ResponseStatus.ERROR,
                message = "Event with code $eventCode not found.",
                data = null,
                error = null
            )

        // only 2 pit scouters
        val usedScouters = if (scouters.size > 2) scouters.take(2) else scouters

        // split the teams evenly between the scouters
        val teams = event.teams.sortedBy { it.number }
        val assignments = teams.mapIndexed { index, team ->
            val scouter = usedScouters[index % usedScouters.size]
            Assignment(
                event = event,
                team = team,
                scouterId = scouter,
                assignmentType = AssignmentType.PIT
            )
        }

        try {
            assignments.forEach { assignmentRepository.persist(it) }

            return ServiceResponse(
                status = ResponseStatus.SUCCESS,
                message = "Pit schedule created successfully.",
                data = Unit,
                error = null
            )
        } catch (ex: Exception) {
            return ServiceResponse(
                status = ResponseStatus.ERROR,
                message = "Failed to create pit schedule: ${ex.message}",
                data = null,
                error = null
            )
        }
    }

    @Transactional
    fun createMatchSchedule(eventCode: String, groupA: Map<AssignmentType, String>, groupB: Map<AssignmentType, String>, groupALead: String, groupBLead: String): ServiceResponse<Unit> {
        val event = eventRepository.findById(eventCode)
            ?: return ServiceResponse(
                status = ResponseStatus.ERROR,
                message = "Event with code $eventCode not found.",
                data = null,
                error = null
            )

        val matches = event.matches.sortedBy { it.matchNumber() }

        // 6 on 6 off for groups
        val assignments = matches.flatMapIndexed { index, match ->
            val isGroupATurn = (index / 6) % 2 == 0
            val group = if (isGroupATurn) groupA else groupB

            group.entries.mapIndexed { idx, entry ->
                val assignmentType = entry.key
                val scouterId = entry.value
                val isLead = if (isGroupATurn) (scouterId == groupALead) else (scouterId == groupBLead)

                val isQualitative = assignmentType == AssignmentType.QUALITATIVE_MATCH
                val matchTeams = listOfNotNull(
                    match.redOne,
                    match.redTwo,
                    match.redThree,
                    match.blueOne,
                    match.blueTwo,
                    match.blueThree
                )

                val team = if (isQualitative) null else matchTeams.getOrNull(idx % matchTeams.size)

                userDirectoryService.addUserRole(scouterId, if (assignmentType == AssignmentType.QUALITATIVE_MATCH) KeycloakRoles.QUALITATIVE_MATCH_SCOUTER else KeycloakRoles.QUANTITATIVE_MATCH_SCOUTER)

                if (isLead) {
                    userDirectoryService.addUserRole(scouterId, KeycloakRoles.SHIFT_LEADER)
                }

                Assignment(
                    event = event,
                    match = match,
                    team = team,
                    scouterId = scouterId,
                    assignmentType = assignmentType
                )
            }
        }

        try {
            assignments.forEach { assignmentRepository.persist(it) }
            return ServiceResponse(
                status = ResponseStatus.SUCCESS,
                message = "Match schedule created successfully.",
                data = Unit,
                error = null
            )
        } catch (ex: Exception) {
            return ServiceResponse(
                status = ResponseStatus.ERROR,
                message = "Failed to create match schedule: ${ex.message}",
                data = null,
                error = null
            )
        }
    }

    fun insertBackScouters(scouters: List<String>): ServiceResponse<Unit> {
        try {
            scouters.forEach { scouterId ->
                userDirectoryService.addUserRole(scouterId, KeycloakRoles.BACK_SCOUTER)
            }
            return ServiceResponse(
                status = ResponseStatus.SUCCESS,
                message = "Back scouters inserted successfully.",
                data = Unit,
                error = null
            )
        } catch (ex: Exception) {
            return ServiceResponse(
                status = ResponseStatus.ERROR,
                message = "Failed to insert back scouters: ${ex.message}",
                data = null,
                error = null
            )
        }
    }

    @Transactional
    fun getPitSchedule(eventCode: String): ServiceResponse<List<Assignment>> {
        val event = eventRepository.findById(eventCode)
            ?: return ServiceResponse(
                status = ResponseStatus.ERROR,
                message = "Event with code $eventCode not found.",
                data = null,
                error = null
            )

        val assignments = assignmentRepository.findByEventAndType(event, AssignmentType.PIT)

        if (assignments.isEmpty()) return ServiceResponse(
            status = ResponseStatus.NOT_FOUND,
            message = "No pit assignments found for event $eventCode.",
            data = null,
            error = ServiceError(
                code = ErrorCode.NotFound,
                detail = "No pit assignments found for event $eventCode.",
            )
        )

        return ServiceResponse(
            status = ResponseStatus.SUCCESS,
            message = "Pit schedule retrieved successfully.",
            data = assignments,
            error = null
        )
    }

    @Transactional
    fun getMatchSchedule(eventCode: String): ServiceResponse<List<Assignment>> {
        val event = eventRepository.findById(eventCode)
            ?: return ServiceResponse(
                status = ResponseStatus.ERROR,
                message = "Event with code $eventCode not found.",
                data = null,
                error = null
            )

        val assignments = assignmentRepository.list("event.code = ?1 and assignmentType in (?2, ?3)", event.code, AssignmentType.QUALITATIVE_MATCH, AssignmentType.QUANTITATIVE_MATCH)

        if (assignments.isEmpty()) return ServiceResponse(
            status = ResponseStatus.NOT_FOUND,
            message = "No match assignments found for event $eventCode.",
            data = null,
            error = ServiceError(
                code = ErrorCode.NotFound,
                detail = "No match assignments found for event $eventCode.",
            )
        )

        return ServiceResponse(
            status = ResponseStatus.SUCCESS,
            message = "Match schedule retrieved successfully.",
            data = assignments,
            error = null
        )
    }

    fun clearAssignmentRoles(): ServiceResponse<Unit> {
        try {
            val users = userDirectoryService.listAllUsers()

            users.forEach { user ->
                userDirectoryService.removeUserRole(user.id, KeycloakRoles.PIT_SCOUTER)
                userDirectoryService.removeUserRole(user.id, KeycloakRoles.QUANTITATIVE_MATCH_SCOUTER)
                userDirectoryService.removeUserRole(user.id, KeycloakRoles.QUALITATIVE_MATCH_SCOUTER)
                userDirectoryService.removeUserRole(user.id, KeycloakRoles.SHIFT_LEADER)
                userDirectoryService.removeUserRole(user.id, KeycloakRoles.BACK_SCOUTER)
            }

            return ServiceResponse(
                status = ResponseStatus.SUCCESS,
                message = "Cleared assignment roles from all users successfully.",
                data = Unit,
                error = null
            )
        } catch (ex: Exception) {
            return ServiceResponse(
                status = ResponseStatus.ERROR,
                message = "Failed to clear assignment roles: ${ex.message}",
                data = null,
                error = null
            )
        }
    }

}
