package com.hwanhee.search_github.model.ui

import com.hwanhee.search_github.base.UNDEFINED_ID
import com.hwanhee.search_github.model.dto.OwnerDto
import com.hwanhee.search_github.model.entity.GithubRepositoryOwnerEntity

data class OwnerUIItem (
    val id : Long,
    val name: String,
    val avatarUrl: String,
    val htmlUrl: String,
) {
    companion object {
        fun from(entity: GithubRepositoryOwnerEntity)
            = OwnerUIItem(
                id = entity.id,
                name = entity.name,
                avatarUrl = entity.avatarUrl,
                htmlUrl = entity.htmlUrl,
            )

        fun from(ownerDto: OwnerDto?)
                = OwnerUIItem(
            id = ownerDto?.id ?: UNDEFINED_ID,
            name = ownerDto?.login ?: "",
            avatarUrl = ownerDto?.avatarUrl ?: "",
            htmlUrl = ownerDto?.htmlUrl ?: "",
        )
    }
}