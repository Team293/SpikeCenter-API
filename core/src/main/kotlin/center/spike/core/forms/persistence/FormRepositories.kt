package center.spike.core.forms.persistence

import center.spike.common.forms.ScoutingType
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class FormDefinitionRepository: PanacheRepository<FormDefinition> {
    fun findByType(scoutingType: ScoutingType): FormDefinition? {
        return find("type = ?1", scoutingType).firstResult()
    }
}

@ApplicationScoped
class FormVersionRepository: PanacheRepository<FormVersion> {
    fun findLatestByFormId(formId: Long): FormVersion? {
        return find("form.id = ?1 order by version desc", formId).firstResult()
    }

    fun findByFormIdAndVersion(formId: Long, version: Int): FormVersion? {
        return find("form.id = ?1 and version = ?2", formId, version).firstResult()
    }
}

@ApplicationScoped
class FormResponseRepository: PanacheRepository<FormResponse> {
    fun findByFormId(formId: Long): List<FormResponse> {
        return list("form.id = ?1", formId)
    }

    fun findByFormIdAndEventCode(formId: Long, eventCode: String): List<FormResponse> {
        return list("form.id = ?1 and eventCode = ?2", formId, eventCode)
    }

    fun findByFormIdAndTeamNumber(formId: Long, teamNumber: Long): List<FormResponse> {
        return list("form.id = ?1 and teamNumber = ?2", formId, teamNumber)
    }
}