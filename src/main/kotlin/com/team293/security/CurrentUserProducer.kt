package com.team293.security

import com.team293.domain.entity.UserEntity
import io.quarkus.security.identity.SecurityIdentity
import jakarta.enterprise.context.RequestScoped
import jakarta.enterprise.inject.Produces
import jakarta.inject.Inject
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response;

@RequestScoped
class CurrentUserProducer @Inject constructor(
    private val identity: SecurityIdentity?
) {
    @Produces
    @RequestScoped
    @CurrentUser
    fun provideCurrentUser(): UserEntity {
        identity?.let { identity ->
            if (identity.isAnonymous) throw WebApplicationException("Unauthorized", Response.Status.UNAUTHORIZED)
            val user = identity.getAttribute("user") as? UserEntity
            return user ?: throw WebApplicationException("Unauthorized", Response.Status.UNAUTHORIZED)
        }

        throw WebApplicationException("Unauthorized", Response.Status.UNAUTHORIZED)
    }
}