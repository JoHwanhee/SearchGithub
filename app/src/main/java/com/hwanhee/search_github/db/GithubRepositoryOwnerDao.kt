package com.hwanhee.search_github.db

import androidx.room.Dao
import com.hwanhee.search_github.model.entity.GithubRepositoryOwnerEntity


@Dao
interface GithubRepositoryOwnerDao : BaseDao<GithubRepositoryOwnerEntity>