package center.spike.core.teams.services

import center.spike.common.ResponseStatus
import center.spike.common.ServiceResponse
import center.spike.core.integration.tba.TBAIntegration
import center.spike.core.teams.persistence.Team
import center.spike.core.teams.persistence.TeamRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional

@ApplicationScoped
class TeamService {

    @Inject
    internal lateinit var teamRepository: TeamRepository

    @Inject
    internal lateinit var tbaIntegration: TBAIntegration

    @Transactional
    fun getOrInitializeTeam(teamNumber: Long): ServiceResponse<Team> {
        val team = teamRepository.find("number", teamNumber).firstResult()
            ?: Team(
                number = teamNumber,
                name = tbaIntegration.getTBATeam(teamNumber).data?.nickname ?: "Team $teamNumber"
            ).also { teamRepository.persist(it) }

        return ServiceResponse(
            status = ResponseStatus.SUCCESS,
            message = null,
            data = team,
            error = null
        )
    }
}