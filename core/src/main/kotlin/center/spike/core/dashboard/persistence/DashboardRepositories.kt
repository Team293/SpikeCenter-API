package center.spike.core.dashboard.persistence

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class DashboardChipRepository: PanacheRepository<DashboardChip>