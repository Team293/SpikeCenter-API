package center.spike.core.integration.tba

import center.spike.common.ErrorCode
import center.spike.common.ResponseStatus
import center.spike.common.ServiceError
import center.spike.common.ServiceResponse
import center.spike.core.events.persistence.EventRepository
import center.spike.core.events.persistence.Match
import center.spike.core.teams.persistence.Team
import center.spike.core.teams.services.TeamService
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.rest.client.inject.RestClient
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@ApplicationScoped
class TBAIntegration {

    @Inject
    internal lateinit var eventRepository: EventRepository

    @Inject
    @RestClient
    internal lateinit var tbaEventClient: TBAEventClient

    @Inject
    @RestClient
    internal lateinit var tbaTeamClient: TBATeamClient

    @Inject
    internal lateinit var teamService: TeamService

    @OptIn(ExperimentalTime::class)
    fun getMatchesForEvent(eventCode: String): ServiceResponse<List<Match>> {
        return try {
            val matches = tbaEventClient.getMatchesForEvent(eventCode)
            fun parseTeamKeyToLong(key: String?): Long? =
                key?.replaceFirst(Regex("(?i)^frc"), "")?.filter(Char::isDigit)?.toLongOrNull()

            val allTeamNumbers = matches.flatMap { tBAMatch ->
                listOfNotNull(
                    tBAMatch.alliances?.red?.team_keys?.getOrNull(0),
                    tBAMatch.alliances?.red?.team_keys?.getOrNull(1),
                    tBAMatch.alliances?.red?.team_keys?.getOrNull(2),
                    tBAMatch.alliances?.blue?.team_keys?.getOrNull(0),
                    tBAMatch.alliances?.blue?.team_keys?.getOrNull(1),
                    tBAMatch.alliances?.blue?.team_keys?.getOrNull(2)
                ).mapNotNull { parseTeamKeyToLong(it) }
            }.toSet()

            val teamMap: Map<Long, Team> = allTeamNumbers.associateWith { teamNumber ->
                teamService.getOrInitializeTeam(teamNumber).data!!
            }

            val event = eventRepository.findById(eventCode)
                ?: throw IllegalStateException("Event with code $eventCode not found")
            val mappedMatches = matches.map { tBAMatch ->
                Match(
                    code = tBAMatch.key ?: throw IllegalStateException("TBA match key is null"),
                    event = event,
                    startTime = tBAMatch.predicted_time?.let { Instant.fromEpochMilliseconds(it) }
                        ?: Instant.fromEpochMilliseconds(0L),
                    redOne = teamMap[parseTeamKeyToLong(tBAMatch.alliances?.red?.team_keys?.getOrNull(0))]!!,
                    redTwo = teamMap[parseTeamKeyToLong(tBAMatch.alliances?.red?.team_keys?.getOrNull(1))]!!,
                    redThree = teamMap[parseTeamKeyToLong(tBAMatch.alliances?.red?.team_keys?.getOrNull(2))]!!,
                    blueOne = teamMap[parseTeamKeyToLong(tBAMatch.alliances?.blue?.team_keys?.getOrNull(0))]!!,
                    blueTwo = teamMap[parseTeamKeyToLong(tBAMatch.alliances?.blue?.team_keys?.getOrNull(1))]!!,
                    blueThree = teamMap[parseTeamKeyToLong(tBAMatch.alliances?.blue?.team_keys?.getOrNull(2))]!!,
                )
            }

            ServiceResponse(
                status = ResponseStatus.SUCCESS,
                message = "Fetched matches from TBA",
                data = mappedMatches,
                error = null
            )
        } catch (e: Exception) {
            ServiceResponse(
                status = ResponseStatus.ERROR,
                message = "Failed to fetch matches from TBA: ${e.message}",
                data = null,
                error = ServiceError(
                    code = ErrorCode.Custom("TBA_INTEGRATION_ERROR"),
                    detail = e.message ?: "Unknown error"
                )
            )
        }
    }


    fun getTBATeam(teamNumber: Long): ServiceResponse<TBATeam> {
        return try {
            val tbaTeam = tbaTeamClient.getTeamInfo("frc$teamNumber")
            ServiceResponse(
                status = ResponseStatus.SUCCESS,
                message = "Fetched team from TBA",
                data = tbaTeam,
                error = null
            )
        } catch (e: Exception) {
            ServiceResponse(
                status = ResponseStatus.ERROR,
                message = "Failed to fetch team from TBA: ${e.message}",
                data = null,
                error = ServiceError(code = ErrorCode.Custom("TBA_INTEGRATION_ERROR"), detail = e.message ?: "Unknown error")
            )
        }
    }

    fun getEvent(eventCode: String): ServiceResponse<TBAEvent> {
        return try {
            val tbaEvent = tbaEventClient.getEventInfo(eventCode)
            ServiceResponse(
                status = ResponseStatus.SUCCESS,
                message = "Fetched event from TBA",
                data = tbaEvent,
                error = null
            )
        } catch (e: Exception) {
            ServiceResponse(
                status = ResponseStatus.ERROR,
                message = "Failed to fetch event from TBA: ${e.message}",
                data = null,
                error = ServiceError(code = ErrorCode.Custom("TBA_INTEGRATION_ERROR"), detail = e.message ?: "Unknown error")
            )
        }
    }

    fun getTeamsForEvent(eventCode: String): ServiceResponse<List<TBATeam>> {
        return try {
            val tbaTeams = tbaEventClient.getTeamsForEvent(eventCode)
            ServiceResponse(
                status = ResponseStatus.SUCCESS,
                message = "Fetched teams for event from TBA",
                data = tbaTeams,
                error = null
            )
        } catch (e: Exception) {
            ServiceResponse(
                status = ResponseStatus.ERROR,
                message = "Failed to fetch teams for event from TBA: ${e.message}",
                data = null,
                error = ServiceError(code = ErrorCode.Custom("TBA_INTEGRATION_ERROR"), detail = e.message ?: "Unknown error")
            )
        }
    }
}