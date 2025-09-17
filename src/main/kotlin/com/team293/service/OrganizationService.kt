package com.team293.service

import com.team293.domain.entity.OrganizationEntity
import com.team293.domain.entity.OrganizationMemberEntity
import com.team293.domain.entity.OrganizationRole
import com.team293.domain.repository.OrganizationRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class OrganizationService @Inject constructor(
    private val orgRepo: OrganizationRepository
) {
    /**
     * Creates a new organization with the specified team number, team name, and owner user ID.
     * @param teamNumber The team number of the organization.
     * @param teamName The team name of the organization.
     * @param ownerUserId The owner's user ID for the organization.
     * @return The created OrganizationEntity.
     */
    fun createOrganization(teamNumber: Int, teamName: String, ownerUserId: String): OrganizationEntity {
        // create entities
        val org = OrganizationEntity(teamNumber = teamNumber, teamName = teamName)
        val member = OrganizationMemberEntity(userId = ownerUserId, team = org, role = OrganizationRole.OWNER)

        // add member to the list of users in an org
        org.members += member

        // persist in database
        member.persist()
        org.persist()

        return org
    }
}