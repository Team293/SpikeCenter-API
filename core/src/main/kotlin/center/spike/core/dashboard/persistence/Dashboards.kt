package center.spike.core.dashboard.persistence

import center.spike.common.dashboard.FilterType
import center.spike.common.dashboard.RenderType
import center.spike.common.forms.FormType
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.*
import kotlinx.serialization.Serializable

@Entity
@Table(name = "dashboard_chip")
@Serializable
class DashboardChip(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "code", nullable = false)
    var code: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "dashboard_chip_filters",
        joinColumns = [JoinColumn(name = "dashboard_chip_id")]
    )
    @Column(name = "filter", nullable = false)
    @Enumerated(EnumType.STRING)
    var filters: MutableList<FilterType> = mutableListOf(),

    @Column(name = "render_type", nullable = false)
    @Enumerated(EnumType.STRING)
    var renderType: RenderType,

    @Column(name = "form_input_type", nullable = false)
    @Enumerated(EnumType.STRING)
    var formInputType: FormType,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "variable_name", nullable = true)
    var variableName: String? = null,
) : PanacheEntityBase
