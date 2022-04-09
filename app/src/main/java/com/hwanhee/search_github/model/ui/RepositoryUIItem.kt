package com.hwanhee.search_github.model.ui

import com.hwanhee.search_github.model.ResultWrapper
import com.hwanhee.search_github.model.dto.ItemDto
import com.hwanhee.search_github.model.dto.RepositoryResponseDto
import com.hwanhee.search_github.model.entity.GithubRepositoryItemAndOwner
import com.hwanhee.search_github.model.ui.OwnerUIItem.Companion.from

data class RepositoryUIItem (
    val id : Long,
    val fullName : String,
    val isPrivate: Boolean,
    val ownerUIItem: OwnerUIItem,
    val description : String,
    val htmlUrl : String,
    val homepage : String,
    val stargazersCount : Int,
    val language : String,
) {
    companion object {
        fun from(entity: GithubRepositoryItemAndOwner)
            = RepositoryUIItem(
                id = entity.itemEntity.id,
                fullName = entity.itemEntity.fullName,
                isPrivate = entity.itemEntity.isPrivate,
                ownerUIItem = from(entity.ownersEntity),
                description = entity.itemEntity.description,
                htmlUrl = entity.itemEntity.htmlUrl,
                homepage = entity.itemEntity.homepage,
                stargazersCount = entity.itemEntity.stargazersCount,
                language = entity.itemEntity.language
            )

        fun from(dto: ItemDto): RepositoryUIItem
                = RepositoryUIItem(
                    id = dto.id ?: 0,
                    fullName = dto.fullName ?: "",
                    isPrivate = dto.private ?: false,
                    ownerUIItem = from(dto.ownerDto),
                    description = dto.description ?: "",
                    htmlUrl = dto.htmlUrl?: "",
                    homepage = dto.homepage?: "",
                    stargazersCount = dto.stargazersCount?: 0,
                    language = dto.language?: "",
                )
    }
}

data class RepositoryUIItems (
    private val _items: List<RepositoryUIItem>
) {
    val items get() = _items.asIterable()

    companion object {
        fun from(entities: List<GithubRepositoryItemAndOwner>)
            = RepositoryUIItems(entities.map { RepositoryUIItem.from(it) })

        fun from(dto: RepositoryResponseDto)
            = RepositoryUIItems(dto.items.map { RepositoryUIItem.from(it) })
    }
}