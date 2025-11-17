package center.spike.api.dashboards

import center.spike.common.dashboard.CreateDashboardChipRequest
import center.spike.common.toResponse
import center.spike.core.dashboard.services.DashboardService
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response

@Path("/dashboard/chips")
class DashboardChipsResource {
    @Inject
    internal lateinit var dashboardService: DashboardService

    @POST
    @Path("/create")
    fun createChip(request: CreateDashboardChipRequest) =
        dashboardService.createChip(request).toResponse()

    @GET
    @Path("/get/{id}")
    fun getChip(@PathParam("id") id: Long) =
        dashboardService.getChip(id).toResponse()
}