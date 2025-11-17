package center.spike.core.schedule.persistence

import center.spike.common.events.AssignmentType
import center.spike.core.events.persistence.Event
import center.spike.core.events.persistence.Match
import center.spike.core.teams.persistence.Team
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import kotlinx.serialization.Serializable

@Entity
@Table(name = "assignments")
@Serializable
class Assignment (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "match_id", nullable = true)
    val match: Match? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_code", nullable = false)
    val event: Event,

    @Column(name = "assignment_type", nullable = false)
    @Enumerated(EnumType.STRING)
    val assignmentType: AssignmentType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_number", nullable = true)
    val team: Team? = null,

    @Column(name = "scouter_id", nullable = false)
    val scouterId: String,
) : PanacheEntityBase