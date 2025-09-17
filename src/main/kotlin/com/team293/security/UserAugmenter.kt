package com.team293.security

import com.team293.domain.repository.UserRepository
import io.quarkus.security.identity.AuthenticationRequestContext
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.identity.SecurityIdentityAugmentor
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import io.smallrye.common.annotation.Blocking
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.jwt.JsonWebToken

@ApplicationScoped
@Blocking
class UserAugmenter @Inject constructor(
    private val jwt: JsonWebToken,
    private val userRepository: UserRepository,
): SecurityIdentityAugmentor {

    override fun augment(
        identity: SecurityIdentity,
        request: AuthenticationRequestContext
    ): Uni<SecurityIdentity> {
        if (identity.isAnonymous) return Uni.createFrom().item(identity)

        val user = userRepository.findById(jwt.subject) ?: throw IllegalStateException("User not found: ${jwt.subject}")

        return Uni.createFrom().item(
            QuarkusSecurityIdentity.builder(identity)
                .addAttribute("user", user)
                .build()
        )
    }

    // run first
    override fun priority() = 0
}