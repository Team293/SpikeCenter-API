package center.spike.api.forms

import center.spike.common.ResponseStatus
import center.spike.common.ServiceResponse
import center.spike.common.forms.CreateFormRequest
import center.spike.common.forms.CreateVersionRequest
import center.spike.common.forms.FormIdTransit
import center.spike.common.forms.FormType
import center.spike.common.forms.GetFormByTypeRequest
import center.spike.common.forms.GetVersionRequest
import center.spike.common.forms.SubmitFormRequest
import center.spike.common.toResponse
import center.spike.core.forms.services.FormService
import center.spike.core.forms.persistence.FormDefinition
import center.spike.core.forms.persistence.FormVersion
import io.quarkus.security.Authenticated
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response

//@Authenticated
@Path("/forms")
class FormsResource {

    @Inject
    lateinit var formService: FormService

    @GET
    @Path("/all")
    fun getAllForms(): Response {
        return formService.getAllForms().toResponse()
    }

    @POST
//    @RolesAllowed("admin")
    @Path("/create")
    fun createForm(request: CreateFormRequest): Response {
        return formService.createForm(request).toResponse()
    }

    @GET
    @Path("/{formId}")
    fun getForm(formId: Long): Response {
        val request = FormIdTransit(formId)
        return formService.getForm(request).toResponse()
    }

    @GET
    @Path("/type/{type}")
    fun getFormByType(type: FormType): Response {
        val request = GetFormByTypeRequest(type)
        return formService.getFormByType(request).toResponse()
    }

    @POST
//    @RolesAllowed("admin")
    @Path("/version/create")
    fun createVersion(request: CreateVersionRequest): Response {
        return formService.createVersion(request).toResponse()
    }

    @GET
    @Path("/version/{formId}/latest")
    fun getLatestVersion(formId: Long): Response {
        val request = FormIdTransit(formId)
        return formService.getLatestVersion(request).toResponse()
    }

    @GET
    @Path("/version/specific/{formId}/{version}")
    fun getSpecificVersion(formId: Long, version: Int): Response {
        val request = GetVersionRequest(formId, version)
        return formService.getSpecificVersion(request).toResponse()
    }

    @POST
    @Path("/submit")
    fun submitForm(request: SubmitFormRequest): Response {
        return formService.submitForm(request).toResponse()
    }

    @GET
    @Path("/responses/{formId}")
    fun getFormResponses(formId: Long): Response {
        val request = FormIdTransit(formId)
        return formService.getFormResponses(request).toResponse()
    }

}