package com.hwanhee.search_github.db

import androidx.room.Dao
import androidx.room.Query
import com.hwanhee.search_github.model.entity.GithubTopicEntity


@Dao
interface GithubTopicDao : BaseDao<GithubTopicEntity> {
    @Query("SELECT * FROM github_topic WHERE repository_id = :id")
    suspend fun findGithubTopicsByRepositoryId(id: Long): List<GithubTopicEntity>
}