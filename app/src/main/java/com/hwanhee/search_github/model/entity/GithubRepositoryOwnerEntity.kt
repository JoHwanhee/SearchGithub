package com.hwanhee.search_github.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "github_repository_owner")
data class GithubRepositoryOwnerEntity (
    @PrimaryKey val id : Long,
    @ColumnInfo(name ="avatar_url") val avatarUrl: String,
    @ColumnInfo(name ="html_url") val htmlUrl: String,
    @ColumnInfo(name ="type") val type: String,
)