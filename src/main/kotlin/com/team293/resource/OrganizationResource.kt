package com.team293.resource

import com.team293.domain.dto.CreateOrganizationRequest
import com.team293.domain.entity.UserEntity
import com.team293.security.CurrentUser
import com.team293.service.OrganizationService
import jakarta.inject.Inject
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/organizations")
class OrganizationResource @Inject constructor(
    private val orgService: OrganizationService,
    @param:CurrentUser private val currentUser: UserEntity
) {
    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    fun createOrganization(req: CreateOrganizationRequest) =
        orgService.createOrganization(
            teamNumber = req.number,
            teamName = req.name,
            ownerUserId = currentUser.id
        )
}