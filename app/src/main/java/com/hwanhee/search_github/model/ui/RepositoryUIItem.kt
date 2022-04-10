package com.hwanhee.search_github.model.ui

import com.hwanhee.search_github.base.UNDEFINED_ID
import com.hwanhee.search_github.model.dto.ItemDto
import com.hwanhee.search_github.model.dto.RepositoryResponseDto
import com.hwanhee.search_github.model.entity.GithubRepositoryItemAndOwner
import com.hwanhee.search_github.model.ui.OwnerUIItem.Companion.from
import okhttp3.internal.toImmutableList

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
                    id = dto.id ?: UNDEFINED_ID,
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
    private var _items: MutableList<RepositoryUIItem> = mutableListOf(),
    private var _total: Int = 0,
) {
    val items get() = _items.toImmutableList()
    val total get() = _total
    val isEmpty get() = _items.isEmpty()

    fun clear() {
        _items.clear()
    }

    operator fun plus(target: RepositoryUIItems) : RepositoryUIItems {
        val newItem = RepositoryUIItems(this._items, target._total)
        val diffArr = (this - target).items
        newItem._items.addAll(diffArr)
        return newItem
    }

    operator fun minus(target: RepositoryUIItems) : RepositoryUIItems {
        val mutableList = mutableListOf<RepositoryUIItem>()
        target.items.forEach { t ->
            this.items.find { it.id == t.id }
                      .let {
                          if (it == null)
                              mutableList.add(t)
                      }
        }
        return RepositoryUIItems(mutableList, this.total - target.total)
    }

    companion object {
        fun from(entities: List<GithubRepositoryItemAndOwner>)
                = RepositoryUIItems(entities.map { RepositoryUIItem.from(it) }
            .toMutableList(),
            entities.count()
        )

        fun from(dto: RepositoryResponseDto)
                = RepositoryUIItems(dto.items.map { RepositoryUIItem.from(it) }
            .toMutableList(),
            dto.totalCount ?: dto.items.count()
        )
    }
}