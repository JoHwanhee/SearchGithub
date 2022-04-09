package com.hwanhee.search_github.db

import androidx.room.*
import com.hwanhee.search_github.model.entity.*


@Dao
interface GithubTopicDao : BaseDao<GithubTopicEntity> {
    @Query("SELECT * FROM github_topic WHERE repository_id = :id")
    suspend fun findGithubTopicsByRepositoryId(id: Long): List<GithubTopicEntity>
}