package center.spike.core.events.persistence

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class EventRepository : PanacheRepositoryBase<Event, String>

@ApplicationScoped
class MatchRepository : PanacheRepository<Match>