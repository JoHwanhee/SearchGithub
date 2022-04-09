package com.hwanhee.search_github.db

import androidx.room.*
import com.hwanhee.search_github.model.entity.SearchWordEntity
import java.time.LocalDate
import java.time.LocalDateTime


@Dao
interface SearchWordDao : BaseDao<SearchWordEntity> {
    @Query("SELECT * FROM search_word WHERE created_at >= :date")
    suspend fun findSearchWordGreaterThen(date: LocalDateTime): List<SearchWordEntity>
}