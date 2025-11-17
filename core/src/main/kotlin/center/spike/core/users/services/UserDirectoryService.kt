package center.spike.core.users.services

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jetbrains.annotations.ApiStatus
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.UserRepresentation

// No ServiceResponse wrapper here since this is an internal service
@ApplicationScoped
@ApiStatus.Internal
class UserDirectoryService {
    @Inject
    internal lateinit var keycloak: Keycloak

    @Inject
    @ConfigProperty(name = "quarkus.oidc.realm")
    internal lateinit var realm: String

    fun listAllUsers(): List<UserRepresentation> =
        keycloak.realm(realm).users().list()

    fun listUsers(first: Int, max: Int): List<UserRepresentation> =
        keycloak.realm(realm).users().list(first, max)

    fun countUsers(): Int =
        keycloak.realm(realm).users().count()

    fun usersWithRole(role: KeycloakRoles): List<UserRepresentation> {
        val role = keycloak.realm(realm).roles().get(role.roleName).toRepresentation()
        return keycloak.realm(realm).users().list().filter { user ->
            keycloak.realm(realm).users().get(user.id).roles().realmLevel().listAll().any { it.id == role.id }
        }
    }

    fun addUserRole(userId: String, role: KeycloakRoles) {
        val role = keycloak.realm(realm).roles().get(role.roleName).toRepresentation()
        keycloak.realm(realm).users().get(userId).roles().realmLevel().add(listOf(role))
    }

    fun removeUserRole(userId: String, role: KeycloakRoles) {
        val role = keycloak.realm(realm).roles().get(role.roleName).toRepresentation()
        keycloak.realm(realm).users().get(userId).roles().realmLevel().remove(listOf(role))
    }
}

@ApiStatus.Internal
enum class KeycloakRoles(val roleName: String) {
    ADMIN("admin"),
    SHIFT_LEADER("shift-leader"),
    PIT_SCOUTER("pit-scouter"),
    QUALITATIVE_MATCH_SCOUTER("qualitative-match-scouter"),
    QUANTITATIVE_MATCH_SCOUTER("quant-match-scouter"),
    BACK_SCOUTER("back-scouter")
}