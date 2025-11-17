package center.spike.core.schedule.persistence

import center.spike.common.events.AssignmentType
import center.spike.core.events.persistence.Event
import center.spike.core.events.persistence.Match
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class MatchRepository : PanacheRepository<Match>

@ApplicationScoped
class AssignmentRepository : PanacheRepository<Assignment> {
    fun findByEventAndType(event: Event, assignmentType: AssignmentType): List<Assignment> {
        return list("event.code = ?1 and assignmentType = ?2", event.code, assignmentType)
    }
}