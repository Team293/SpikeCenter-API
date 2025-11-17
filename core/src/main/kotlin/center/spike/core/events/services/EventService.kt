package center.spike.core.events.services

import center.spike.common.ResponseStatus
import center.spike.common.ServiceResponse
import center.spike.core.events.persistence.Event
import center.spike.core.events.persistence.EventRepository
import center.spike.core.integration.tba.TBAIntegration
import center.spike.core.teams.services.TeamService
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import org.hibernate.Hibernate
import java.time.Instant as JInstant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@ApplicationScoped
class EventService {

    @Inject
    internal lateinit var teamService: TeamService

    @Inject
    internal lateinit var eventRepository: EventRepository

    @Inject
    internal lateinit var tbaIntegration: TBAIntegration

    @OptIn(ExperimentalTime::class)
    @Transactional
    fun getOrInitializeEvent(eventCode: String): ServiceResponse<Event> {
        var event = eventRepository.findById(eventCode)

        if (event == null) {
            val eventInfo = tbaIntegration.getEvent(eventCode)

            val tbaTeams = tbaIntegration.getTeamsForEvent(eventCode)

            if (eventInfo.status != ResponseStatus.SUCCESS || eventInfo.data == null) {
                return ServiceResponse(
                    status = ResponseStatus.ERROR,
                    message = "Failed to retrieve event info from TBA: ${eventInfo.message}",
                    data = null,
                    error = eventInfo.error
                )
            }

            if (tbaTeams.status != ResponseStatus.SUCCESS) {
                return ServiceResponse(
                    status = ResponseStatus.ERROR,
                    message = "Failed to retrieve event teams from TBA: ${tbaTeams.message}",
                    data = null,
                    error = tbaTeams.error
                )
            }

            val teams = tbaTeams.data!!.map { tbaTeam ->
                teamService.getOrInitializeTeam(
                    tbaTeam.team_number ?: throw IllegalStateException("TBA team number is null")
                ).data!!
            }
            fun toInstant(dateStr: String, zone: ZoneId = ZoneOffset.UTC): Instant {
                val localDate = LocalDate.parse(dateStr) // expects "yyyy-MM-dd"
                val jInstant: JInstant = localDate.atStartOfDay(zone).toInstant()
                return Instant.fromEpochMilliseconds(jInstant.toEpochMilli())
            }

            val eventStart = eventInfo.data!!.start_date?.let { toInstant(it) } ?: Instant.fromEpochMilliseconds(0)
            val eventEnd = eventInfo.data!!.end_date?.let { toInstant(it) } ?: Instant.fromEpochMilliseconds(0)

            event = Event(
                code = eventCode,
                name = eventInfo.data!!.name ?: "Event $eventCode",
                startDate = eventStart,
                endDate = eventEnd,
                teams = teams.toMutableList()
            )

            eventRepository.persist(event)

            val matches = tbaIntegration.getMatchesForEvent(eventCode)

            if (matches.status != ResponseStatus.SUCCESS) {
                return ServiceResponse(
                    status = ResponseStatus.ERROR,
                    message = "Failed to retrieve event matches from TBA: ${matches.message}",
                    data = null,
                    error = matches.error
                )
            }

            event.matches = matches.data!!.toMutableList()
        }

        Hibernate.initialize(event.matches)

        return ServiceResponse(
            status = ResponseStatus.SUCCESS,
            message = "Successfully retrieved or initialized event with code $eventCode",
            data = event,
            error = null
        )
    }

}