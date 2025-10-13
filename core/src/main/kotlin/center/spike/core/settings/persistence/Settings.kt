package center.spike.core.settings.persistence

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "settings")
class Settings {
    @Id
    var id: Long = 1

    @Column(unique = true, nullable = false)
    var eventCode: String = ""
}

@ApplicationScoped
class SettingsRepository: PanacheRepository<Settings> {
    fun getSettings(): Settings {
        return findById(1) ?: Settings().also { persist(it) }
    }
}