package com.team293.resource

import com.team293.domain.entity.UserEntity
import com.team293.security.CurrentUser
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/")
class RootResource @Inject constructor(
    @param:CurrentUser private val currentUser: UserEntity
) {
    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    fun getMe() = currentUser
}