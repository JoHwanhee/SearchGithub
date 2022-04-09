package com.hwanhee.search_github.db

import androidx.room.*
import com.hwanhee.search_github.model.entity.GithubRepositoryOwnerEntity
import com.hwanhee.search_github.model.entity.SearchWordEntity
import java.time.LocalDate
import java.time.LocalDateTime


@Dao
interface GithubRepositoryOwnerDao : BaseDao<GithubRepositoryOwnerEntity>