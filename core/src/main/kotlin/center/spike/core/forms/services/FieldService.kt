package center.spike.core.forms.services

import center.spike.common.ErrorCode
import center.spike.common.ResponseStatus
import center.spike.common.ServiceError
import center.spike.common.ServiceResponse
import center.spike.common.forms.SubmitFormRequest
import center.spike.common.forms.fields.FieldAnswer
import center.spike.common.forms.fields.FieldDefinition
import center.spike.core.forms.fields.RadioAnswer
import center.spike.core.forms.fields.Validator
import center.spike.core.forms.persistence.FormVersionRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional

@ApplicationScoped
class FieldService {

    @Inject
    internal lateinit var formVersionRepository: FormVersionRepository

    /**
     * Given a list of [FieldAnswer]s, return the corresponding list of [FieldDefinition]s.
     */
    @Transactional
    fun getFieldDefinitions(request: SubmitFormRequest): ServiceResponse<List<FieldDefinition>> {
        val version = formVersionRepository.findLatestByFormId(request.formId)
            ?: return ServiceResponse(
                status = ResponseStatus.NOT_FOUND,
                message = "Form version not found",
                data = null,
                error = ServiceError(ErrorCode.NotFound, "Form version for form id ${request.formId} not found")
            )

        val schema = version.schema
        val fieldDefinitions = mutableListOf<FieldDefinition>()

        for (answer in request.fieldResponses) {
            val fieldId = answer.fieldId
            val fieldDef = schema.find { it.id == fieldId }

            if (fieldDef != null) {
                (fieldDefinitions).add(fieldDef)
            } else {
                return ServiceResponse(
                    status = ResponseStatus.ERROR,
                    message = "Field definition not found",
                    data = null,
                    error = ServiceError(ErrorCode.NotFound, "Field definition with id $fieldId not found in schema")
                )
            }
        }

        return ServiceResponse(ResponseStatus.SUCCESS, null, fieldDefinitions)
    }

    fun validateField(fieldDef: FieldDefinition, answer: FieldAnswer): ServiceResponse<Unit> {
        val validator = Validator.getValidator(fieldDef) ?: return ServiceResponse(
            status = ResponseStatus.ERROR,
            message = "No validator found for field type ${fieldDef.type}",
            data = null,
            error = ServiceError(ErrorCode.Validation, "No validator for field type ${fieldDef.type}")
        )

        val result = validator.validate(fieldDef, answer)

        return if (result.isNotEmpty()) {
            ServiceResponse(
                status = ResponseStatus.ERROR,
                message = "Validation failed",
                data = null,
                error = ServiceError(ErrorCode.Validation, result.joinToString("; "))
            )
        } else {
            ServiceResponse(ResponseStatus.SUCCESS, null, Unit)
        }
    }
}