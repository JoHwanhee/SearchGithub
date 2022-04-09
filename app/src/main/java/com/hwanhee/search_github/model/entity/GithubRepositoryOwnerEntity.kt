package com.hwanhee.search_github.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hwanhee.search_github.model.dto.ItemDto
import com.hwanhee.search_github.model.dto.OwnerDto

@Entity(tableName = "github_repository_owner")
data class GithubRepositoryOwnerEntity (
    @PrimaryKey val id : Long,
    @ColumnInfo(name ="avatar_url") val avatarUrl: String,
    @ColumnInfo(name ="html_url") val htmlUrl: String,
    @ColumnInfo(name ="type") val type: String,
) {
    companion object {
        fun from(ownerDto: OwnerDto)
                = GithubRepositoryOwnerEntity(
            ownerDto.id ?: 0,
            ownerDto.avatarUrl ?: "",
            ownerDto.htmlUrl ?: "",
            ownerDto.type ?: "",
        )
    }
}