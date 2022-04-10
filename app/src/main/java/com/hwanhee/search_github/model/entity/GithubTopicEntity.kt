package com.hwanhee.search_github.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "github_topic", indices = [Index(value = ["repository_id", "topic"], unique = true)])
data class GithubTopicEntity(
    @ColumnInfo(name = "repository_id") val repositoryId: Long,
    @ColumnInfo(name = "topic") val topic: String,

    @PrimaryKey(autoGenerate = true) val id: Long = 0,
) {
    companion object {
        fun from(repositoryId: Long, topic: String)
                = GithubTopicEntity(
            repositoryId,
            topic,
        )
    }
}