package com.hwanhee.search_github.model.entity

import androidx.room.Embedded
import androidx.room.Relation

data class GithubRepositoryItemByOwner(
    @Embedded val ownersEntity: GithubRepositoryOwnerEntity,
    @Relation(
         parentColumn = "id",
         entityColumn = "owner_id"
    )
    val itemEntity: List<GithubRepositoryItemEntity>,
)