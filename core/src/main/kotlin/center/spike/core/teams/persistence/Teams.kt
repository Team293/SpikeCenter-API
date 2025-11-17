package center.spike.core.teams.persistence

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kotlinx.serialization.Serializable

@Entity
@Table(name = "teams")
@Serializable
class Team (
    @Id
    val number: Long,

    @Column(name = "name", nullable = false)
    val name: String,
) : PanacheEntityBase

@ApplicationScoped
class TeamRepository : PanacheRepository<Team>