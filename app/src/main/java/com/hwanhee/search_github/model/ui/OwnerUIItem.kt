package com.hwanhee.search_github.model.ui

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.hwanhee.search_github.model.dto.OwnerDto
import com.hwanhee.search_github.model.entity.GithubRepositoryItemAndOwner
import com.hwanhee.search_github.model.entity.GithubRepositoryOwnerEntity
import java.time.LocalDateTime

data class OwnerUIItem (
    val id : Long,
    val avatarUrl: String,
    val htmlUrl: String,
) {
    companion object {
        fun from(entity: GithubRepositoryOwnerEntity)
            = OwnerUIItem(
                id = entity.id,
                avatarUrl = entity.avatarUrl,
                htmlUrl = entity.htmlUrl,
            )

        fun from(ownerDto: OwnerDto?)
                = OwnerUIItem(
            id = ownerDto?.id ?: 0,
            avatarUrl = ownerDto?.avatarUrl ?: "",
            htmlUrl = ownerDto?.htmlUrl ?: "",
        )
    }
}