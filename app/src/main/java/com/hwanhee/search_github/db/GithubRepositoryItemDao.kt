package com.hwanhee.search_github.db

import androidx.room.*
import com.hwanhee.search_github.model.entity.GithubRepositoryItemAndOwner
import com.hwanhee.search_github.model.entity.GithubRepositoryItemByOwner
import com.hwanhee.search_github.model.entity.GithubRepositoryItemEntity
import com.hwanhee.search_github.model.entity.SearchWordEntity
import java.time.LocalDate
import java.time.LocalDateTime


@Dao
interface GithubRepositoryItemDao : BaseDao<GithubRepositoryItemEntity> {
    @Query("SELECT * FROM github_repository_item WHERE id = :id")
    suspend fun findGithubRepositoryAndOwnersByRepositoryId(id: Long): List<GithubRepositoryItemAndOwner>

    @Query("SELECT * FROM github_repository_owner WHERE id = :id")
    suspend fun findGithubRepositoryAndOwnersByOwnerId(id: Long): List<GithubRepositoryItemByOwner>
}