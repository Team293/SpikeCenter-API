package center.spike.core.forms.persistence

import center.spike.common.forms.ScoutingType
import center.spike.common.forms.fields.FieldAnswer
import center.spike.common.forms.fields.FieldDefinition
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Entity
@Table(name = "forms", uniqueConstraints = [UniqueConstraint(columnNames = ["type"])])
@Serializable
class FormDefinition @OptIn(ExperimentalTime::class) constructor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    var type: ScoutingType,

    @Column(name = "version")
    var version: Int = 1,

    @OneToMany(mappedBy = "form", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @Transient
    var versions: MutableList<FormVersion> = mutableListOf(),

    @Column(name = "created_at", nullable = true)
    var createdAt: Instant = Clock.System.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Clock.System.now(),
): PanacheEntityBase

@Entity
@Table(name = "form_versions")
@Serializable
class FormVersion(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    var form: FormDefinition,

    @Column(name = "version", nullable = false)
    var version: Int,

    @Lob
    @Column(name = "schema", nullable = false, columnDefinition = "TEXT")
    var schema: List<FieldDefinition>
): PanacheEntityBase

@Entity
@Table(name = "form_responses")
@Serializable
class FormResponse @OptIn(ExperimentalTime::class) constructor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "team_number", nullable = false)
    var teamNumber: Long,

    @Column(name = "event_code", nullable = false)
    var eventCode: String,

    @Column(name = "match_code", nullable = true)
    var matchCode: String? = null,

    @Column(name = "response_type", nullable = false)
    @Enumerated(EnumType.STRING)
    var responseType: ScoutingType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    var form: FormDefinition,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_version_id", nullable = false)
    var formVersion: FormVersion,

    @Column(name = "submitted_at", nullable = false)
    var submittedAt: Instant = Clock.System.now(),

    @Lob
    @Column(name = "data", nullable = false, columnDefinition = "TEXT")
    var data: List<FieldAnswer>,
): PanacheEntityBase
