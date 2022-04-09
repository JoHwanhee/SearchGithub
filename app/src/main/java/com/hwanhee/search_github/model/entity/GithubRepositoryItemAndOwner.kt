package com.hwanhee.search_github.model.entity

import androidx.room.Embedded
import androidx.room.Relation

data class GithubRepositoryItemAndOwner(
    @Embedded val itemEntity: GithubRepositoryItemEntity,
    @Relation(
         parentColumn = "owner_id",
         entityColumn = "id"
    )
    val ownersEntity: GithubRepositoryOwnerEntity
)