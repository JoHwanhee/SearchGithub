package com.hwanhee.search_github.db

import androidx.room.Dao
import androidx.room.Query
import com.hwanhee.search_github.model.entity.GithubRepositoryItemAndOwner
import com.hwanhee.search_github.model.entity.GithubRepositoryItemByOwner
import com.hwanhee.search_github.model.entity.GithubRepositoryItemEntity


@Dao
interface GithubRepositoryItemDao : BaseDao<GithubRepositoryItemEntity> {
    @Query("SELECT * FROM github_repository_item WHERE id = :id")
    suspend fun findGithubRepositoryAndOwnersByRepositoryId(id: Long): List<GithubRepositoryItemAndOwner>

    @Query("SELECT * FROM github_repository_owner WHERE id = :id")
    suspend fun findGithubRepositoryAndOwnersByOwnerId(id: Long): List<GithubRepositoryItemByOwner>

    @Query("SELECT * FROM github_repository_item " +
            "LEFT JOIN github_topic " +
            "ON github_repository_item.id = github_topic.repository_id " +
            "WHERE github_repository_item.id != -1 " +
            "AND github_topic.topic LIKE :searchWord " +
            "OR github_repository_item.name LIKE :searchWord " +
            "OR github_repository_item.full_name LIKE :searchWord " +
            "OR github_repository_item.description LIKE :searchWord " +
            "OR language LIKE :searchWord " +
            "GROUP BY github_repository_item.id "  +
            "ORDER BY github_repository_item.created_at DESC "
    )
    suspend fun search(searchWord: String): List<GithubRepositoryItemAndOwner>

    @Query("SELECT * FROM github_repository_item " +
            "LEFT JOIN github_topic " +
            "ON github_repository_item.id = github_topic.repository_id " +
            "WHERE github_repository_item.id != -1 " +
            "AND (github_topic.topic LIKE :searchWord " +
            "OR github_repository_item.name LIKE :searchWord " +
            "OR github_repository_item.full_name LIKE :searchWord) " +
            "OR github_repository_item.description LIKE :searchWord " +
            "AND language LIKE :language " +
            "GROUP BY github_repository_item.id " +
            "ORDER BY github_repository_item.created_at DESC "
    )
    suspend fun searchByLanguage(searchWord: String, language: String): List<GithubRepositoryItemAndOwner>
}