package com.team293.domain.repository

import com.team293.domain.entity.OrganizationEntity
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrganizationRepository : PanacheRepositoryBase<OrganizationEntity, Int>