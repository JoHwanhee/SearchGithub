package com.hwanhee.search_github.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "github_topic")
data class GithubTopicEntity(
    @ColumnInfo(name = "repository_id") val repositoryId: Long,
    @ColumnInfo(name = "topic") val topic: String,

    @PrimaryKey(autoGenerate = true) val id: Long = 0,
)