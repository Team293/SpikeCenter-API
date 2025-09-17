package com.team293.domain.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.*

@Entity
@Table(name = "organizations")
class OrganizationEntity(
    @Id
    @Column(name = "team_number", nullable = false)
    val teamNumber: Int,

    @Column(name = "team_name", nullable = false)
    val teamName: String,

    @OneToMany(mappedBy = "team", cascade = [CascadeType.ALL], orphanRemoval = true)
    val members: MutableSet<OrganizationMemberEntity> = mutableSetOf()
) : PanacheEntityBase

@Entity
@Table(name = "organization_members")
class OrganizationMemberEntity(
    @Column(name = "user_id", nullable = false)
    val userId: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_number", referencedColumnName = "team_number")
    val team: OrganizationEntity,

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    val role: OrganizationRole = OrganizationRole.MEMBER
) : PanacheEntity()

enum class OrganizationRole {
    OWNER,
    ADMIN,
    MEMBER
}