package com.hwanhee.search_github.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hwanhee.search_github.base.UNDEFINED_ID
import com.hwanhee.search_github.model.dto.ItemDto
import java.time.LocalDateTime

@Entity(tableName = "github_repository_item")
data class GithubRepositoryItemEntity (
    @PrimaryKey val id : Long,
    @ColumnInfo(name ="name") val name : String,
    @ColumnInfo(name ="full_name") val fullName : String,
    @ColumnInfo(name ="is_private") val isPrivate: Boolean,
    @ColumnInfo(name ="owner_id") val ownerId: Long,
    @ColumnInfo(name ="description") val description : String,
    @ColumnInfo(name ="html_url") val htmlUrl : String,
    @ColumnInfo(name ="homepage") val homepage : String,
    @ColumnInfo(name ="stargazers_count") val stargazersCount : Int,
    @ColumnInfo(name ="watchers_count") val watchersCount : Int,
    @ColumnInfo(name ="forks_count") val forksCount: Int,
    @ColumnInfo(name ="language") val language : String,
    @ColumnInfo(name ="disabled") val disabled : Boolean,
    @ColumnInfo(name = "created_at") val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun from(itemDto: ItemDto)
                = GithubRepositoryItemEntity(
                itemDto.id ?: UNDEFINED_ID,
            itemDto.name ?: "",
            itemDto.fullName ?: "",
            itemDto.private ?: false,
            itemDto.ownerDto?.id ?: UNDEFINED_ID,
            itemDto.description ?: "",
            itemDto.htmlUrl ?: "",
            itemDto.homepage ?: "",
            itemDto.stargazersCount ?: 0,
            itemDto.watchersCount ?: 0,
            itemDto.forksCount ?: 0,
            itemDto.language ?: "",
            itemDto.disabled ?: false,
            )
    }
}