package center.spike.core.forms.services

import center.spike.common.ErrorCode
import center.spike.common.ResponseStatus
import center.spike.common.SafeRunner
import center.spike.common.ServiceError
import center.spike.common.ServiceResponse
import center.spike.common.forms.CreateFormRequest
import center.spike.common.forms.CreateVersionRequest
import center.spike.common.forms.FormIdTransit
import center.spike.common.forms.FormResponsesResponse
import center.spike.common.forms.FormVersionResponse
import center.spike.common.forms.GetFormByTypeRequest
import center.spike.common.forms.GetFormResponsesWithEventCodeRequest
import center.spike.common.forms.GetFormResponsesWithTeamNumberRequest
import center.spike.common.forms.GetVersionRequest
import center.spike.common.forms.SetVersionRequest
import center.spike.common.forms.SubmitFormRequest
import center.spike.common.toServiceResponse
import center.spike.core.settings.services.SettingsService
import center.spike.core.forms.persistence.FieldDefinitionListConverter
import center.spike.core.forms.persistence.FormDefinition
import center.spike.core.forms.persistence.FormDefinitionRepository
import center.spike.core.forms.persistence.FormResponse
import center.spike.core.forms.persistence.FormResponseRepository
import center.spike.core.forms.persistence.FormVersion
import center.spike.core.forms.persistence.FormVersionRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import kotlin.time.ExperimentalTime

@ApplicationScoped
class FormService {
    @Inject
    internal lateinit var formDefinitionRepository: FormDefinitionRepository

    @Inject
    internal lateinit var formVersionRepository: FormVersionRepository

    @Inject
    internal lateinit var fieldService: FieldService

    @Inject
    internal lateinit var formResponseRepository: FormResponseRepository

    @Inject
    internal lateinit var settingsService: SettingsService

    @OptIn(ExperimentalTime::class)
    @Transactional
    fun createForm(request: CreateFormRequest): ServiceResponse<FormDefinition> {
        val definition = FormDefinition(type = request.type)

        return SafeRunner.runOutcome(
            errorCode = { ErrorCode.Database }
        ) {
            formDefinitionRepository.persist(definition)
            definition
        }.toServiceResponse(
            onSuccess = { ServiceResponse(ResponseStatus.SUCCESS, null, definition) },
            onFailure = { err -> ServiceResponse(ResponseStatus.ERROR, "Failed to create form", null, err) }
        )
    }

    @Transactional
    fun createVersion(request: CreateVersionRequest): ServiceResponse<FormVersion> {
        val definition = formDefinitionRepository.findById(request.formId)
            ?: return ServiceResponse(
                status = ResponseStatus.NOT_FOUND,
                message = "Form definition not found",
                data = null,
                error = ServiceError(ErrorCode.NotFound, "Form definition with id ${request.formId} not found")
            )

        val converter = FieldDefinitionListConverter()
        val schemaList = converter.convertToEntityAttribute(request.schema.toString())
            ?: return ServiceResponse(
                status = ResponseStatus.ERROR,
                message = "Invalid schema format",
                data = null,
                error = ServiceError(ErrorCode.Validation, "Schema format is invalid")
            )

        val version = FormVersion(
            form = definition,
            version = definition.version + 1,
            schema = schemaList.toMutableList()
        )

        return SafeRunner.runOutcome(
            errorCode = { ErrorCode.Database }
        ) {
            definition.version = version.version
            formDefinitionRepository.persist(definition)
            formVersionRepository.persist(version)
            version
        }.toServiceResponse(
            onSuccess = { ServiceResponse(ResponseStatus.SUCCESS, null, version) },
            onFailure = { err -> ServiceResponse(ResponseStatus.ERROR, "Failed to create form version", null, err) }
        )
    }

    @Transactional
    fun getForm(request: FormIdTransit): ServiceResponse<FormDefinition> {
        val definition = formDefinitionRepository.findById(request.formId)
            ?: return ServiceResponse(
                status = ResponseStatus.NOT_FOUND,
                message = "Form definition not found",
                data = null,
                error = ServiceError(ErrorCode.NotFound, "Form definition with id ${request.formId} not found")
            )

        return ServiceResponse(ResponseStatus.SUCCESS, null, definition)
    }

    @Transactional
    fun getLatestVersion(request: FormIdTransit): ServiceResponse<FormVersionResponse> {
        val version = formVersionRepository.findLatestByFormId(request.formId)
            ?: return ServiceResponse(
                status = ResponseStatus.NOT_FOUND,
                message = "Form version not found",
                data = null,
                error = ServiceError(ErrorCode.NotFound, "Form version for form id ${request.formId} not found")
            )

        val response = FormVersionResponse(
            id = version.id!!,
            formId = version.form.id!!,
            version = version.version,
            schema = version.schema
        )

        return ServiceResponse(ResponseStatus.SUCCESS, null, response)
    }

    @Transactional
    fun getCurrentVersion(request: FormIdTransit): ServiceResponse<FormVersionResponse> {
        val definition = formVersionRepository.findById(request.formId)
            ?: return ServiceResponse(
                status = ResponseStatus.NOT_FOUND,
                message = "Form definition not found",
                data = null,
                error = ServiceError(ErrorCode.NotFound, "Form definition with id ${request.formId} not found")
            )

        val version = formVersionRepository.findByFormIdAndVersion(request.formId, definition.version)
            ?: return ServiceResponse(
                status = ResponseStatus.NOT_FOUND,
                message = "Form version not found",
                data = null,
                error = ServiceError(ErrorCode.NotFound, "Form version ${definition.version} for form id ${request.formId} not found")
            )

        val response = FormVersionResponse(
            id = version.id!!,
            formId = version.form.id!!,
            version = version.version,
            schema = version.schema
        )

        return ServiceResponse(ResponseStatus.SUCCESS, null, response)
    }

    @Transactional
    fun setVersion(request: SetVersionRequest): ServiceResponse<FormVersionResponse> {
        val definition = formDefinitionRepository.findById(request.formId)
            ?: return ServiceResponse(
                status = ResponseStatus.NOT_FOUND,
                message = "Form definition not found",
                data = null,
                error = ServiceError(ErrorCode.NotFound, "Form definition with id ${request.formId} not found")
            )

        val version = formVersionRepository.findByFormIdAndVersion(request.formId, request.version)
            ?: return ServiceResponse(
                status = ResponseStatus.NOT_FOUND,
                message = "Form version not found",
                data = null,
                error = ServiceError(ErrorCode.NotFound, "Form version ${request.version} for form id ${request.formId} not found")
            )

        definition.version = request.version

        return SafeRunner.runOutcome(
            errorCode = { ErrorCode.Database }
        ) {
            formDefinitionRepository.persist(definition)
            definition
        }.toServiceResponse(
            onSuccess = {
                val response = FormVersionResponse(
                    id = version.id!!,
                    formId = version.form.id!!,
                    version = version.version,
                    schema = version.schema
                )
                ServiceResponse(ResponseStatus.SUCCESS, "Form version set successfully", response)
            },
            onFailure = { err -> ServiceResponse(ResponseStatus.ERROR, "Failed to set form version", null, err) }
        )
    }

    @Transactional
    fun getSpecificVersion(request: GetVersionRequest): ServiceResponse<FormVersionResponse> {
        val version = formVersionRepository.findByFormIdAndVersion(request.formId, request.version)
            ?: return ServiceResponse(
                status = ResponseStatus.NOT_FOUND,
                message = "Form version not found",
                data = null,
                error = ServiceError(ErrorCode.NotFound, "Form version ${request.version} for form id ${request.formId} not found")
            )

        val response = FormVersionResponse(
            id = version.id!!,
            formId = version.form.id!!,
            version = version.version,
            schema = version.schema
        )

        return ServiceResponse(ResponseStatus.SUCCESS, null, response)
    }

    @OptIn(ExperimentalTime::class)
    @Transactional
    fun submitForm(request: SubmitFormRequest): ServiceResponse<Unit> {
        val definition = formDefinitionRepository.findById(request.formId)
            ?: return ServiceResponse(
                status = ResponseStatus.NOT_FOUND,
                message = "Form definition not found",
                data = null,
                error = ServiceError(ErrorCode.NotFound, "Form definition with id ${request.formId} not found")
            )

        val version = formVersionRepository.findLatestByFormId(request.formId)
            ?: return ServiceResponse(
                status = ResponseStatus.NOT_FOUND,
                message = "Form version not found",
                data = null,
                error = ServiceError(ErrorCode.NotFound, "Form version for form id ${request.formId} not found")
            )

        val fieldDefsResponse = fieldService.getFieldDefinitions(request)

        if (fieldDefsResponse.status != ResponseStatus.SUCCESS) {
            return ServiceResponse(
                status = ResponseStatus.ERROR,
                message = "Failed to validate field responses",
                data = null,
                error = fieldDefsResponse.error
            )
        }

        val errors = mutableMapOf<String, String>()

        // validate each field against definition
        for (answer in request.fieldResponses) {
            val response = fieldService.validateField(
                fieldDef = fieldDefsResponse.data!!.first { it.id == answer.fieldId },
                answer = answer
            )

            if (response.status != ResponseStatus.SUCCESS) {
                errors[answer.fieldId] = response.error?.detail ?: "Unknown error"
            }
        }

        if (errors.isNotEmpty()) {
            return ServiceResponse(
                status = ResponseStatus.ERROR,
                message = "Validation failed for some fields",
                data = null,
                error = ServiceError(ErrorCode.Validation, errors.toString())
            )
        }

        val response = FormResponse(
            form = definition,
            teamNumber = request.teamNumber,
            responseType = definition.type,
            eventCode = settingsService.getEventCode(),
            formVersion = version,
            matchCode = request.matchCode,
            data = request.fieldResponses
        )

        return SafeRunner.runOutcome(
            errorCode = { ErrorCode.Database }
        ) {
            formResponseRepository.persist(response)
        }.toServiceResponse(
            onSuccess = { ServiceResponse(ResponseStatus.SUCCESS, "Form submitted successfully", Unit) },
            onFailure = { err -> ServiceResponse(ResponseStatus.ERROR, "Failed to submit form", null, err) }
        )
    }

    @Transactional
    fun getFormByType(request: GetFormByTypeRequest): ServiceResponse<FormDefinition> {
        val definition = formDefinitionRepository.findByType(request.type)
            ?: return ServiceResponse(
                status = ResponseStatus.NOT_FOUND,
                message = "Form definition not found",
                data = null,
                error = ServiceError(ErrorCode.NotFound, "Form definition with type ${request.type} not found")
            )

        return ServiceResponse(ResponseStatus.SUCCESS, null, definition)
    }

    @Transactional
    fun getAllForms(): ServiceResponse<List<FormDefinition>> {
        val definitions = formDefinitionRepository.listAll()
        return ServiceResponse(ResponseStatus.SUCCESS, null, definitions)
    }

    @Transactional
    fun getFormResponses(request: FormIdTransit): ServiceResponse<List<FormResponsesResponse>> {
        val responses = formResponseRepository.findByFormId(request.formId)
        val response = responses.map { resp ->
            FormResponsesResponse(
                id = resp.id!!,
                formId = resp.form.id!!,
                teamNumber = resp.teamNumber,
                responseType = resp.responseType,
                eventCode = resp.eventCode,
                matchCode = resp.matchCode,
                fieldResponses = resp.data
            )
        }
        return ServiceResponse(ResponseStatus.SUCCESS, null, response)
    }

    @Transactional
    fun deleteForm(request: FormIdTransit): ServiceResponse<Unit> {
        val definition = formDefinitionRepository.findById(request.formId)
            ?: return ServiceResponse(
                status = ResponseStatus.NOT_FOUND,
                message = "Form definition not found",
                data = null,
                error = ServiceError(ErrorCode.NotFound, "Form definition with id ${request.formId} not found")
            )

        return SafeRunner.runOutcome(
            errorCode = { ErrorCode.Database }
        ) {
            formDefinitionRepository.delete(definition)
        }.toServiceResponse(
            onSuccess = { ServiceResponse(ResponseStatus.SUCCESS, "Form deleted successfully", Unit) },
            onFailure = { err -> ServiceResponse(ResponseStatus.ERROR, "Failed to delete form", null, err) }
        )
    }

    @Transactional
    fun getFormResponsesByEvent(request: GetFormResponsesWithEventCodeRequest): ServiceResponse<List<FormResponsesResponse>> {
        val responses = formResponseRepository.findByFormIdAndEventCode(request.formId, request.eventCode)
        val response = responses.map { resp ->
            FormResponsesResponse(
                id = resp.id!!,
                formId = resp.form.id!!,
                teamNumber = resp.teamNumber,
                responseType = resp.responseType,
                eventCode = resp.eventCode,
                matchCode = resp.matchCode,
                fieldResponses = resp.data
            )
        }
        return ServiceResponse(ResponseStatus.SUCCESS, null, response)
    }

    @Transactional
    fun getFormResponsesByTeam(request: GetFormResponsesWithTeamNumberRequest): ServiceResponse<List<FormResponsesResponse>> {
        val responses = formResponseRepository.findByFormIdAndTeamNumber(request.formId, request.teamNumber)
        val response = responses.map { resp ->
            FormResponsesResponse(
                id = resp.id!!,
                formId = resp.form.id!!,
                teamNumber = resp.teamNumber,
                responseType = resp.responseType,
                eventCode = resp.eventCode,
                matchCode = resp.matchCode,
                fieldResponses = resp.data
            )
        }
        return ServiceResponse(ResponseStatus.SUCCESS, null, response)
    }
}