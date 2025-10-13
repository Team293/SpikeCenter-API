package center.spike.core.forms.persistence

import center.spike.common.forms.FormType
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class FormDefinitionRepository: PanacheRepository<FormDefinition> {
    fun findByType(formType: FormType): FormDefinition? {
        return find("type = ?1", formType).firstResult()
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
}