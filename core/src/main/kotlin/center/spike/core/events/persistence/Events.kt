package center.spike.core.events.persistence

import center.spike.core.teams.persistence.Team
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Entity
@Table(name = "matches")
@Serializable
class Match (
    @Id
    val code: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_code", nullable = false)
    @Serializable(with = EventCodeSerializer::class)
    var event: Event,

    @Column(name = "start_time", nullable = false)
    val startTime: Instant,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "red_one", nullable = false)
    val redOne: Team,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "red_two", nullable = false)
    val redTwo: Team,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "red_three", nullable = false)
    val redThree: Team,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "blue_one", nullable = false)
    val blueOne: Team,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "blue_two", nullable = false)
    val blueTwo: Team,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "blue_three", nullable = false)
    val blueThree: Team
) : PanacheEntityBase {

    val matchNumber: () -> Int
        get() = {
            val parts = code.split("_qm")
            if (parts.size == 2) {
                parts[1].toIntOrNull() ?: 0
            } else {
                0
            }
        }
}

@Entity
@Table(name = "events")
@Serializable
class Event @OptIn(ExperimentalTime::class) constructor(
    @Id
    val code: String,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "start_date", nullable = false)
    val startDate: Instant,

    @Column(name = "end_date", nullable = false)
    val endDate: Instant,

    @OneToMany(mappedBy = "event", cascade = [CascadeType.ALL], orphanRemoval = true)
    var matches: MutableList<Match> = mutableListOf(),

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "event_teams",
        joinColumns = [JoinColumn(name = "event_code")],
        inverseJoinColumns = [JoinColumn(name = "team_number")]
    )
    val teams: MutableList<Team> = mutableListOf()
) : PanacheEntityBase