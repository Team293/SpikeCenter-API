package center.spike.core.settings.services

import center.spike.core.settings.persistence.Settings
import center.spike.core.settings.persistence.SettingsRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional

// NOTE: this is the ONLY service where ServiceResponse will not be used... as settings is unique and values are always present
@ApplicationScoped
class SettingsService {
    @Inject
    internal lateinit var settingsRepository: SettingsRepository

    @Transactional
    fun getEventCode(): String {
        val settings: Settings = settingsRepository.getSettings()
        return settings.eventCode
    }
}