package com.hwanhee.search_github.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "search_word")
data class SearchWordEntity(
    @PrimaryKey(autoGenerate = false) val id: Long,
    @ColumnInfo(name = "keyword") val keyword: String,
    @ColumnInfo(name = "language") val language: String,
    @ColumnInfo(name = "created_at") val createdAt: LocalDateTime = LocalDateTime.now(),
)